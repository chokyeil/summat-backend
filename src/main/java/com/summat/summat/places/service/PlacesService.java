package com.summat.summat.places.service;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.dto.places.PlaceMainListResDto;
import com.summat.summat.places.dto.places.PlacesDetailResDto;
import com.summat.summat.places.dto.places.PlacesReqDto;
import com.summat.summat.places.entity.PlaceLike;
import com.summat.summat.places.entity.PlaceTag;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlaceLikeRepository;
import com.summat.summat.places.repository.PlaceTagRepository;
import com.summat.summat.places.repository.PlacesRepository;
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

    public boolean createdPlace(PlacesReqDto placesReqDto, MultipartFile image, Long userId) {


        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));


        log.info("placesReqDto.getPlaceName() = " + placesReqDto.getPlaceName());
        log.info("placesReqDto.getPlaceLotAddress() = " + placesReqDto.getPlaceLotAddress());
        log.info("placesReqDto.getPlaceRoadAddress() = " + placesReqDto.getPlaceRoadAddress());
        if(placesReqDto.getPlaceName() == null || placesReqDto.getPlaceLotAddress() == null || placesReqDto.getPlaceRoadAddress() == null) {
            log.info("여기로 빠지니???!!!");
            return false;
        }
        log.info("PlacesService 진입!!");
        String imageUrl = savePlaceImage(image);

        Places place = new Places();
        place.setPlaceName(placesReqDto.getPlaceName());
        place.setPlaceLotAddress(placesReqDto.getPlaceLotAddress());
        place.setPlaceRoadAddress(placesReqDto.getPlaceRoadAddress());
        place.setPlaceDescription(placesReqDto.getPlaceDescription());
        place.setPlaceImageUrl(imageUrl);
        place.setOneLineDesc(placesReqDto.getOneLineDesc());
        place.setPlaceType(placesReqDto.getPlaceType());
        place.setPlaceRegion(placesReqDto.getPlaceRegion());
        place.setUsers(user);



        for(String str : placesReqDto.getTags()) {
            log.info("tag type = " + str);
            PlaceTagType tagType = PlaceTagType.fromCode(str);

            log.info("place tag type = " + tagType);


            PlaceTag placeTag = new PlaceTag();
            placeTag.setPlace(place);
            placeTag.setTagType(tagType);

            place.getPlaceTags().add(placeTag);
        }

        placesRepository.save(place);

        return true;


    }

    public List<PlaceMainListResDto> getPlacesList(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Places> pageListPlaces = placesRepository.findAll(pageable);

        List<PlaceMainListResDto> placeMainListResDtoList = new ArrayList<>();

        for(Places place : pageListPlaces) {
            List<PlaceTagType> placeTags = placeTagRepository.findTagTypesByPlaceId(place.getId());

            PlaceMainListResDto placeMainListResDto = new PlaceMainListResDto();

            placeMainListResDto.setPlaceId(place.getId());
            placeMainListResDto.setPlaceName(place.getPlaceName());
            placeMainListResDto.setPlaceImageUrl(place.getPlaceImageUrl());
            placeMainListResDto.setPlaceLotAddress(place.getPlaceLotAddress());
            placeMainListResDto.setPlaceRoadAddress(place.getPlaceRoadAddress());
            placeMainListResDto.setOneLineDesc(place.getOneLineDesc());
            placeMainListResDto.setTags(placeTags);
            placeMainListResDto.setLikeCount(place.getLikeCount());
            placeMainListResDto.setViewCount(place.getViewCount());


            placeMainListResDtoList.add(placeMainListResDto);

        }

//        List<Places> listPlaces = placesRepository.findAll();

//        return listPlaces.size() > 0 ? listPlaces : null;
//        return pageListPlaces.getSize() > 0 ? pageListPlaces : null;
        return placeMainListResDtoList;
    }

    public boolean updatePlace(PlacesReqDto placesReqDto, MultipartFile image, Long userId, Long placeId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Optional<Places> findByPlace = placesRepository.findById(placeId);

        Places updatePlace = findByPlace.orElseThrow(null);

        if(updatePlace == null) return false;

        String imageUrl = savePlaceImage(image);

        updatePlace.setPlaceName(placesReqDto.getPlaceName());
        updatePlace.setPlaceLotAddress(placesReqDto.getPlaceLotAddress());
        updatePlace.setPlaceRoadAddress(placesReqDto.getPlaceRoadAddress());
        updatePlace.setPlaceDescription(placesReqDto.getPlaceDescription());
        updatePlace.setPlaceImageUrl(imageUrl);
        updatePlace.setOneLineDesc(placesReqDto.getOneLineDesc());
        updatePlace.setPlaceType(placesReqDto.getPlaceType());
        updatePlace.setPlaceRegion(placesReqDto.getPlaceRegion());
        updatePlace.setLikeCount(findByPlace.get().getLikeCount());
        updatePlace.setViewCount(findByPlace.get().getViewCount());
        updatePlace.setUsers(user);

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

        PlacesDetailResDto changeDetailPlace = new PlacesDetailResDto();

        changeDetailPlace.setPlaceName(detailPlace.get().getPlaceName());
        changeDetailPlace.setPlaceLotAddress(detailPlace.get().getPlaceLotAddress());
        changeDetailPlace.setPlaceRoadAddress(detailPlace.get().getPlaceRoadAddress());
        changeDetailPlace.setOneLineDesc(detailPlace.get().getOneLineDesc());
        changeDetailPlace.setPlaceDescription(detailPlace.get().getPlaceDescription());
        changeDetailPlace.setPlaceType(detailPlace.get().getPlaceType());
        changeDetailPlace.setPlaceRegion(detailPlace.get().getPlaceRegion());
        changeDetailPlace.setLikeCount(detailPlace.get().getLikeCount());
        changeDetailPlace.setViewCount(detailPlace.get().getViewCount());

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

    public List<PlaceMainListResDto> searchSummatList(String query, String region, String type, List<String> tags) {

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


        List<Places> places = placesRepository.searchPlacesExistsTags(query, region, type, tagTypes);


        List<PlaceMainListResDto> result = new ArrayList<>();
        for (Places place : places) {

            List<PlaceTagType> dtoTags = new ArrayList<>();
            for (PlaceTag pt : place.getPlaceTags()) {
                dtoTags.add(pt.getTagType());
            }

            PlaceMainListResDto dto = new PlaceMainListResDto();
            dto.setPlaceId(place.getId());
            dto.setPlaceName(place.getPlaceName());
            dto.setPlaceImageUrl(place.getPlaceImageUrl());
            dto.setPlaceLotAddress(place.getPlaceLotAddress());
            dto.setPlaceRoadAddress(place.getPlaceRoadAddress());
            dto.setOneLineDesc(place.getOneLineDesc());
            dto.setTags(dtoTags);
            dto.setLikeCount(place.getLikeCount());
            dto.setViewCount(place.getViewCount());

            result.add(dto);
        }

        return result;
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
