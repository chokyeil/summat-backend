package com.summat.summat.places.repository;

import com.summat.summat.places.entity.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

    Optional<PlaceLike> findByUserIdAndPlaceId(Long userId, Long placeId);


}
