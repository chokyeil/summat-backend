package com.summat.summat.places.repository;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.entity.Places;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Long> {

    @Modifying
    @Query("UPDATE Places p SET p.viewCount = p.viewCount + 1 WHERE p.id = :placeId")
    boolean increaseViews(@Param("placeId") Long placeId);

    @Query("""
            select p
            from Places p
            where
                (:q is null or :q = '' or
                    lower(p.placeName) like lower(concat('%', :q, '%')) or
                    lower(p.placeLotAddress) like lower(concat('%', :q, '%')) or
                    lower(p.placeRoadAddress) like lower(concat('%', :q, '%'))
                )
              and (:region is null or :region = '' or p.placeRegion = :region)
              and (:type is null or :type = '' or p.placeType = :type)
              and (
                    :tagTypes is null
                    or exists (
                        select 1
                        from PlaceTag pt
                        where pt.place = p and pt.tagType in :tagTypes
                    )
              )
            order by p.createdAt desc
            """)
    List<Places> searchPlacesExistsTags(
            @Param("q") String q,
            @Param("region") String region,
            @Param("type") String type,
            @Param("tagTypes") List<PlaceTagType> tagTypes
    );

}
