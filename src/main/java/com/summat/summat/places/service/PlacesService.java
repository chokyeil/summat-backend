package com.summat.summat.places.service;

import com.summat.summat.places.dto.PlacesDetailResDto;
import com.summat.summat.places.dto.PlacesReqDto;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.users.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final PlacesRepository placesRepository;
    public boolean createdPlace(PlacesReqDto placesReqDto, MultipartFile image, Users user) {
        log.info("placesReqDto.getPlaceName() = " + placesReqDto.getPlaceName());
        log.info("placesReqDto.getPlaceDetailAddress() = " + placesReqDto.getPlaceDetailAddress());
        if(placesReqDto.getPlaceName() == null || placesReqDto.getPlaceDetailAddress() == null) {
            log.info("여기로 빠지니???!!!");
            return false;
        }
        log.info("PlacesService 진입!!");
        String imageUrl = savePlaceImage(image);

        Places place = new Places();
        place.setPlaceName(placesReqDto.getPlaceName());
        place.setPlaceDetailAddress(placesReqDto.getPlaceDetailAddress());
        place.setPlaceDescription(placesReqDto.getPlaceDescription());
        place.setPlaceImageUrl(imageUrl);
        place.setOneLineDesc(placesReqDto.getOneLineDesc());
        place.setPlaceType(placesReqDto.getPlaceType());
        place.setPlaceRegion(placesReqDto.getPlaceRegion());
        place.setLikeCount(placesReqDto.getLikeCount());
        place.setViewCount(placesReqDto.getViewCount());
        place.setUsers(user);

        placesRepository.save(place);

        return true;


    }

    public List<Places> getPlacesList() {

        List<Places> listPlaces = placesRepository.findAll();

        return listPlaces.size() > 0 ? listPlaces : null;
    }

    public boolean updatePlace(PlacesReqDto placesReqDto, Users user, Long placeId) {
        Optional<Places> findByPlace = placesRepository.findById(placeId);

        Places updatePlace = findByPlace.orElseThrow(null);

        if(updatePlace == null) return false;

        updatePlace.setPlaceName(placesReqDto.getPlaceName());
        updatePlace.setPlaceDetailAddress(placesReqDto.getPlaceDetailAddress());
        updatePlace.setOneLineDesc(placesReqDto.getOneLineDesc());
        updatePlace.setPlaceDescription(placesReqDto.getPlaceDescription());
        updatePlace.setPlaceType(placesReqDto.getPlaceType());
        updatePlace.setPlaceRegion(placesReqDto.getPlaceRegion());
        updatePlace.setLikeCount(placesReqDto.getLikeCount());
        updatePlace.setViewCount(placesReqDto.getViewCount());
        updatePlace.setUsers(user);

        placesRepository.save(updatePlace);

        return true;
    }

    public boolean removePlace(Long placeId) {
        if(!placesRepository.existsById(placeId)) return false;

        placesRepository.deleteById(placeId);

        return true;
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

    public PlacesDetailResDto detailPlace(Long placeId) {

        Optional<Places> detailPlace = placesRepository.findById(placeId);

        PlacesDetailResDto changeDetailPlace = new PlacesDetailResDto();

        changeDetailPlace.setPlaceName(detailPlace.get().getPlaceName());
        changeDetailPlace.setPlaceDetailAddress(detailPlace.get().getPlaceDetailAddress());
        changeDetailPlace.setOneLineDesc(detailPlace.get().getOneLineDesc());
        changeDetailPlace.setPlaceDescription(detailPlace.get().getPlaceDescription());
        changeDetailPlace.setPlaceType(detailPlace.get().getPlaceType());
        changeDetailPlace.setPlaceRegion(detailPlace.get().getPlaceRegion());
        changeDetailPlace.setLikeCount(detailPlace.get().getLikeCount());
        changeDetailPlace.setViewCount(detailPlace.get().getViewCount());


        return changeDetailPlace;

    }
}
