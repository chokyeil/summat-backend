package com.summat.summat.places.repository;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.dto.places.response.PlacesFindResponseDto;
import com.summat.summat.places.entity.Places;

import com.summat.summat.places.repository.dto.PlacesFindResponseProjection;
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

    @Query(value = "SELECT " +
                        "p.places_id            AS placesId, " +
                        "p.place_name           AS placeName, " +
                        "p.place_image_url      AS placeImageUrl, " +
                        "p.one_line_desc        AS oneLineDesc, " +
                        "p.place_lot_address    AS placeLotAddress, " +
                        "p.place_road_address   AS placeRoadAddress, " +
                        "p.place_type           AS placeType, " +
                        "p.like_count           AS likeCount, " +
                        "p.view_count           AS viewCount," +
                        "p.created_at           AS createdAt " +
                   "FROM places p " +
                   "ORDER BY p.created_at DESC"
            , countQuery = "SELECT count(*) " +
                        "FROM places"
            , nativeQuery = true)
    Page<PlacesFindResponseProjection> findMainList(Pageable pageable);


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
    Page<PlacesFindResponseDto> searchPlacesExistsTags(
            @Param("pageable") Pageable pageable,
            @Param("q") String q,
            @Param("region") String region,
            @Param("type") String type,
            @Param("tagTypes") List<PlaceTagType> tagTypes
    );

}
