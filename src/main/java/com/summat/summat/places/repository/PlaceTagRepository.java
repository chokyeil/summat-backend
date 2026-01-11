package com.summat.summat.places.repository;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.entity.PlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PlaceTagRepository extends JpaRepository<PlaceTag, Long> {

    @Query("SELECT pt.tagType FROM PlaceTag pt WHERE pt.place.id = :placeId")
    List<PlaceTagType> findTagTypesByPlaceId(@Param("placeId") Long placeId);
}
