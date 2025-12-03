package com.summat.summat.places.service;

import com.summat.summat.places.dto.PlacesReqDto;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlacesService {
    private final PlacesRepository placesRepository;
    public boolean createdPlace(PlacesReqDto placesReqDto, Users user) {
        if(placesReqDto.getPlaceName() == null || placesReqDto.getPlaceDetailAddress() == null) return false;

        Places place = new Places();
        place.setPlaceName(placesReqDto.getPlaceName());
        place.setPlaceDetailAddress(placesReqDto.getPlaceDetailAddress());
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
}
