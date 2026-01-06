package com.summat.summat.places.repository;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.entity.PlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PlaceTagRepository extends JpaRepository<PlaceTag, Long> {
    List<PlaceTagType> findByPlaceId(Long placeId);
}
