package com.summat.summat.places.repository;

import com.summat.summat.places.entity.Places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Long> {

    @Modifying
    @Query("UPDATE Places p SET p.viewCount = p.viewCount + 1 WHERE p.id = :placeId")
    int increaseViews(@Param("placeId") Long placeId);
}
