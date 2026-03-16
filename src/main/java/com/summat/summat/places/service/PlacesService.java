package com.summat.summat.places.service;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.enums.RegionType;
import com.summat.summat.places.dto.places.response.PlaceListPageResDto;
import com.summat.summat.places.dto.places.response.PlaceLikeResDto;
import com.summat.summat.places.dto.places.response.PlaceMainListResDto;
import com.summat.summat.places.dto.places.response.PlacesDetailResDto;
import com.summat.summat.places.dto.places.response.PlaceViewResDto;
import com.summat.summat.places.dto.places.request.PlacesReqDto;
import com.summat.summat.places.dto.places.response.PlacesFindResponseDto;
import com.summat.summat.places.entity.PlaceLike;
import com.summat.summat.places.entity.PlaceTag;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlaceLikeRepository;
import com.summat.summat.places.repository.PlaceQueryRepository;
import com.summat.summat.places.repository.PlaceTagRepository;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.places.repository.dto.PlacesFindResponseProjection;
import com.summat.summat.reply.repository.ReplyRepository;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final UsersRepository usersRepository;
    private final PlacesRepository placesRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final PlaceTagRepository placeTagRepository;
    private final PlaceQueryRepository placeQueryRepository;
    private final ReplyRepository replyRepository;

    public boolean createdPlace(PlacesReqDto placesReqDto, MultipartFile image, Long userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        log.info("placesReqDto.getPlaceName() = " + placesReqDto.getPlaceName());
        log.info("placesReqDto.getLotAddress() = " + placesReqDto.getLotAddress());
        log.info("placesReqDto.getRoadAddress() = " + placesReqDto.getRoadAddress());
        if(placesReqDto.getPlaceName() == null || placesReqDto.getLotAddress() == null || placesReqDto.getRoadAddress() == null) {
            log.info("여기로 빠지니???!!!");
            return false;
        }
        log.info("PlacesService 진입!!");
        String imageUrl = savePlaceImage(image);

        Places place = new Places();
        place.setName(placesReqDto.getPlaceName());
        place.setLotAddress(placesReqDto.getLotAddress());
        place.setRoadAddress(placesReqDto.getRoadAddress());
        place.setDescription(placesReqDto.getDescription());
        place.setImageUrl(imageUrl);
        place.setSummary(placesReqDto.getSummary());
        place.setCategory(placesReqDto.getCategory());
        place.setRegion(placesReqDto.getRegion());
        place.setCreatedBy(user);

        if (placesReqDto.getTags() != null) {
            for(String str : placesReqDto.getTags()) {
                log.info("tag type = " + str);
                PlaceTagType tagType = PlaceTagType.fromCode(str);

                log.info("place tag type = " + tagType);

                PlaceTag placeTag = new PlaceTag();
                placeTag.setPlace(place);
                placeTag.setTag(tagType);

                place.getTags().add(placeTag);
            }
        }

        placesRepository.save(place);

        return true;
    }

    public PlaceListPageResDto getPlacesList(Pageable pageable, Long userId) {

        Page<PlacesFindResponseProjection> placesFindResponseList = placesRepository.findMainList(pageable);

        Set<Long> likedPlaceIds = getLikedPlaceIds(userId);

        List<PlaceMainListResDto> placeMainListResDtoList = new ArrayList<>();
        PlaceListPageResDto placeListPageResDto = new PlaceListPageResDto();

        for(PlacesFindResponseProjection placesFindResponse : placesFindResponseList) {
            List<PlaceTagType> placeTags = placeTagRepository.findTagTypesByPlaceId(placesFindResponse.getPlacesId());
            PlaceMainListResDto placeMainListResDto = new PlaceMainListResDto();

            placeMainListResDto.setPlaceId(placesFindResponse.getPlacesId());
            placeMainListResDto.setPlaceName(placesFindResponse.getPlaceName());
            placeMainListResDto.setImageUrl(placesFindResponse.getImageUrl());
            placeMainListResDto.setSummary(placesFindResponse.getSummary());
            placeMainListResDto.setLotAddress(placesFindResponse.getLotAddress());
            placeMainListResDto.setRoadAddress(placesFindResponse.getRoadAddress());
            placeMainListResDto.setCategory(placesFindResponse.getCategory());
            placeMainListResDto.setLikeCount(placesFindResponse.getLikeCount());
            placeMainListResDto.setLiked(likedPlaceIds.contains(placesFindResponse.getPlacesId()));
            placeMainListResDto.setViewCount(placesFindResponse.getViewCount());
            placeMainListResDto.setCreatedAt(placesFindResponse.getCreatedAt());
            placeMainListResDto.setTags(placeTags);

            placeMainListResDtoList.add(placeMainListResDto);
        }

        placeListPageResDto.setPlaceList(placeMainListResDtoList);
        placeListPageResDto.setPage(placesFindResponseList.getNumber());
        placeListPageResDto.setSize(placesFindResponseList.getSize());
        placeListPageResDto.setTotalElements(placesFindResponseList.getTotalElements());
        placeListPageResDto.setTotalPages(placesFindResponseList.getTotalPages());
        placeListPageResDto.setHasNext(placesFindResponseList.hasNext());

        return placeListPageResDto;
    }

    @Transactional
    public boolean updatePlace(PlacesReqDto placesReqDto, MultipartFile image, Long userId, Long placeId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Places updatePlace = placesRepository.findById(placeId).orElse(null);

        if (updatePlace == null) return false;

        String imageUrl = savePlaceImage(image);

        updatePlace.setName(placesReqDto.getPlaceName());
        updatePlace.setLotAddress(placesReqDto.getLotAddress());
        updatePlace.setRoadAddress(placesReqDto.getRoadAddress());
        updatePlace.setDescription(placesReqDto.getDescription());
        if (imageUrl != null) {
            updatePlace.setImageUrl(imageUrl);
        }
        updatePlace.setSummary(placesReqDto.getSummary());
        updatePlace.setCategory(placesReqDto.getCategory());
        updatePlace.setRegion(placesReqDto.getRegion());
        updatePlace.setCreatedBy(user);

        if (placesReqDto.getTags() != null) {
            updatePlace.getTags().clear();
            for (String str : placesReqDto.getTags()) {
                PlaceTagType tagType = PlaceTagType.fromCode(str);
                PlaceTag placeTag = new PlaceTag();
                placeTag.setPlace(updatePlace);
                placeTag.setTag(tagType);
                updatePlace.getTags().add(placeTag);
            }
        }

        placesRepository.save(updatePlace);

        return true;
    }

    @Transactional
    public boolean removePlace(Long userId, Long placeId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Places place = placesRepository.findById(placeId).orElse(null);
        if (place == null) return false;

        if (!place.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 등록한 장소만 삭제할 수 있습니다.");
        }

        // reply FK constraint 방지: 자식 댓글(depth=1) → 부모 댓글(depth=0) 순으로 삭제
        replyRepository.deleteChildRepliesByPlaceId(placeId);
        replyRepository.deleteParentRepliesByPlaceId(placeId);

        placesRepository.deleteById(placeId);

        return true;
    }

    public PlacesDetailResDto detailPlace(Long placeId, Long userId) {

        Places place = placesRepository.findById(placeId).orElse(null);
        if (place == null) return null;
        PlacesDetailResDto changeDetailPlace = new PlacesDetailResDto();

        changeDetailPlace.setPlaceId(place.getId());
        changeDetailPlace.setPlaceName(place.getName());
        changeDetailPlace.setImageUrl(place.getImageUrl());
        changeDetailPlace.setLotAddress(place.getLotAddress());
        changeDetailPlace.setRoadAddress(place.getRoadAddress());
        changeDetailPlace.setSummary(place.getSummary());
        changeDetailPlace.setDescription(place.getDescription());
        changeDetailPlace.setCategory(place.getCategory());
        changeDetailPlace.setRegion(place.getRegion());
        changeDetailPlace.setTags(placeTagRepository.findTagTypesByPlaceId(place.getId()));
        changeDetailPlace.setLikeCount(place.getLikeCount());
        changeDetailPlace.setLiked(userId != null && placeLikeRepository.findByUserIdAndPlaceId(userId, placeId).isPresent());
        changeDetailPlace.setViewCount(place.getViewCount());
        changeDetailPlace.setCreatedAt(place.getCreatedAt());

        return changeDetailPlace;
    }


    @Transactional
    public PlaceViewResDto increaseView(Long placeId) {
        Places place = placesRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        placesRepository.increaseViews(placeId);
        return new PlaceViewResDto(place.getViewCount() + 1);
    }

    @Transactional
    public PlaceLikeResDto toggleLike(Long userId, Long placeId) {

        Places place = placesRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        Optional<PlaceLike> isPlaceLikeId = placeLikeRepository.findByUserIdAndPlaceId(userId, placeId);
        boolean liked;

        if(isPlaceLikeId.isPresent()) {
            placeLikeRepository.delete(isPlaceLikeId.get());
            place.setLikeCount(place.getLikeCount() - 1);
            liked = false;
        } else {
            PlaceLike placeLike = new PlaceLike();
            placeLike.setUserId(userId);
            placeLike.setPlaceId(placeId);
            place.setLikeCount(place.getLikeCount() + 1);
            placeLikeRepository.save(placeLike);
            liked = true;
        }

        return new PlaceLikeResDto(liked, place.getLikeCount());
    }

    public PlaceListPageResDto searchSummatList(Pageable pageable,
                                                String query,
                                                List<String> categories,
                                                List<String> regions,
                                                List<String> tags,
                                                Long userId) {

        // region 전처리: 허용 광역 단위 검증 + "전국" 해석
        List<String> resolvedRegions = new ArrayList<>();
        if (regions != null) {
            for (String r : regions) {
                String trimmed = (r == null) ? "" : r.trim();
                if (trimmed.isBlank() || "전국".equals(trimmed)) {
                    continue; // null/blank/"전국" → 전체 조회로 해석, 필터 제외
                }
                if (RegionType.fromName(trimmed) == null) {
                    throw new IllegalArgumentException("지원하지 않는 지역입니다: " + trimmed);
                }
                resolvedRegions.add(trimmed);
            }
        }

        boolean regionEmpty = resolvedRegions.isEmpty();
        List<String> regionsParam = regionEmpty ? List.of("__DUMMY__") : resolvedRegions;

        List<String> categoriesParam = (categories == null || categories.isEmpty())
                ? List.of("__DUMMY__")
                : categories;

        boolean categoryEmpty = (categories == null || categories.isEmpty());

        boolean qEmpty = (query == null || query.isBlank());

        // prefix LIKE용
        String qPrefix = qEmpty ? "" : query.trim();

        // FULLTEXT용 (boolean mode prefix 검색)
        String against = qEmpty ? "" : query.trim() + "*";

        // 1) tags 파싱(콤마 방어)
        List<String> flatTags = new ArrayList<>();
        if (tags != null) {
            for (String t : tags) {
                for (String part : t.split(",")) {
                    String v = part.trim();
                    if (!v.isBlank()) flatTags.add(v);
                }
            }
        }

        // 2) tagNames: DB에 ENUM STRING(대문자)으로 저장되므로 name()으로 변환
        boolean tagsEmpty = flatTags.isEmpty();
        List<String> tagNamesParam = new ArrayList<>();
        if (!tagsEmpty) {
            for (String tag : flatTags) {
                tagNamesParam.add(PlaceTagType.fromCode(tag).name());
            }
        } else {
            tagNamesParam.add("__DUMMY__");
        }

        Page<PlacesFindResponseProjection> placesFindResponseList = placesRepository.searchPlacesUnified(
                        qEmpty,
                        qPrefix,
                        against,
                        regionsParam,
                        regionEmpty,
                        categoriesParam,
                        categoryEmpty,
                        tagNamesParam,
                        tagsEmpty,
                        pageable
                );

        Set<Long> likedPlaceIds = getLikedPlaceIds(userId);

        List<PlaceMainListResDto> placeMainListResDtoList = new ArrayList<>();
        PlaceListPageResDto placeListPageResDto = new PlaceListPageResDto();

        for(PlacesFindResponseProjection placesFindResponseProjection : placesFindResponseList) {
            List<PlaceTagType> placeTags = placeTagRepository.findTagTypesByPlaceId(placesFindResponseProjection.getPlacesId());
            PlaceMainListResDto placeMainListResDto = new PlaceMainListResDto();

            placeMainListResDto.setPlaceId(placesFindResponseProjection.getPlacesId());
            placeMainListResDto.setPlaceName(placesFindResponseProjection.getPlaceName());
            placeMainListResDto.setImageUrl(placesFindResponseProjection.getImageUrl());
            placeMainListResDto.setSummary(placesFindResponseProjection.getSummary());
            placeMainListResDto.setLotAddress(placesFindResponseProjection.getLotAddress());
            placeMainListResDto.setRoadAddress(placesFindResponseProjection.getRoadAddress());
            placeMainListResDto.setCategory(placesFindResponseProjection.getCategory());
            placeMainListResDto.setLikeCount(placesFindResponseProjection.getLikeCount());
            placeMainListResDto.setLiked(likedPlaceIds.contains(placesFindResponseProjection.getPlacesId()));
            placeMainListResDto.setViewCount(placesFindResponseProjection.getViewCount());
            placeMainListResDto.setCreatedAt(placesFindResponseProjection.getCreatedAt());
            placeMainListResDto.setTags(placeTags);

            placeMainListResDtoList.add(placeMainListResDto);
        }

        placeListPageResDto.setPlaceList(placeMainListResDtoList);
        placeListPageResDto.setPage(placesFindResponseList.getNumber());
        placeListPageResDto.setSize(placesFindResponseList.getSize());
        placeListPageResDto.setTotalElements(placesFindResponseList.getTotalElements());
        placeListPageResDto.setTotalPages(placesFindResponseList.getTotalPages());
        placeListPageResDto.setHasNext(placesFindResponseList.hasNext());

        return placeListPageResDto;
    }


    private Set<Long> getLikedPlaceIds(Long userId) {
        if (userId == null) return new HashSet<>();
        Set<Long> ids = new HashSet<>();
        for (PlaceLike like : placeLikeRepository.findByUserId(userId)) {
            ids.add(like.getPlaceId());
        }
        return ids;
    }

    private String savePlaceImage(MultipartFile image) {
        log.info("savePlaceImage 진입!!");
        if (image == null || image.isEmpty()) {
            return null;
        }

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            Path placeDir = Paths.get(uploadDir, "places");
            Files.createDirectories(placeDir);

            Path savePath = placeDir.resolve(fileName);
            Files.copy(image.getInputStream(), savePath);

            return "/uploads/places/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

}
