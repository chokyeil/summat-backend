package com.summat.summat.places.service;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.dto.places.response.PlaceListPageResDto;
import com.summat.summat.places.dto.places.response.PlaceMainListResDto;
import com.summat.summat.places.dto.places.response.PlacesDetailResDto;
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
import java.util.List;
import java.util.Optional;
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

    public PlaceListPageResDto getPlacesList(Pageable pageable) {

        Page<PlacesFindResponseProjection> placesFindResponseList = placesRepository.findMainList(pageable);

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

    public boolean removePlace(Long userId, Long placeId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if(!placesRepository.existsById(placeId)) return false;

        placesRepository.deleteById(placeId);

        return true;
    }

    public PlacesDetailResDto detailPlace(Long placeId) {

        Optional<Places> detailPlace = placesRepository.findById(placeId);

        Places place = detailPlace.get();
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
        changeDetailPlace.setViewCount(place.getViewCount());
        changeDetailPlace.setCreatedAt(place.getCreatedAt());

        return changeDetailPlace;
    }


    @Transactional
    public boolean increaseView(Long placeId) {
        Places place = placesRepository.findById(placeId).orElseThrow(() -> new IllegalArgumentException("not found place"));

        boolean isCreaseView = placesRepository.increaseViews(placeId);

        return isCreaseView;
    }

    public boolean toggleLike(Long userId, Long placeId) {

//        Users user = usersRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
//
        Places place = placesRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        Optional<PlaceLike> isPlaceLikeId = placeLikeRepository.findByUserIdAndPlaceId(userId, placeId);
        boolean isPlaceLikeResult;

        if(isPlaceLikeId.isPresent()) {
            placeLikeRepository.delete(isPlaceLikeId.get());
            place.setLikeCount(place.getLikeCount() - 1);
            isPlaceLikeResult = false;
        } else {
            PlaceLike placeLike = new PlaceLike();
            placeLike.setUserId(userId);
            placeLike.setPlaceId(placeId);
            place.setLikeCount(place.getLikeCount() + 1);
            placeLikeRepository.save(placeLike);
            isPlaceLikeResult = true;
        }

        return isPlaceLikeResult;
    }

    public PlaceListPageResDto searchSummatList(Pageable pageable,
                                                String query,
                                                List<String> region,
                                                List<String> type,
                                                List<String> tags) {

        List<String> regionsParam = (region == null || region.isEmpty())
                ? List.of("__DUMMY__")   // 절대 존재하지 않을 값
                : region;

        boolean regionEmpty = (region == null || region.isEmpty());

        List<String> typesParam = (type == null || type.isEmpty())
                ? List.of("__DUMMY__")
                : type;

        boolean typeEmpty = (type == null || type.isEmpty());

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

        // 2) tagTypes: 없으면 null
        List<PlaceTagType> tagTypes = null;
        if (!flatTags.isEmpty()) {
            tagTypes = new ArrayList<>();
            for (String tag : flatTags) {
                tagTypes.add(PlaceTagType.fromCode(tag));
            }
        }
//        Page<PlacesFindResponseDto> result = placeQueryRepository.findMainList(query, region, type, pageable);
        Page<PlacesFindResponseProjection> placesFindResponseList = placesRepository.searchPlacesUnified(
                        qEmpty,
                        qPrefix,
                        against,
                        regionsParam,
                        regionEmpty,
                        typesParam,
                        typeEmpty,
                        pageable
                );

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
