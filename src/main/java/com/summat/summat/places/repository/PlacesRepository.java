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


    @Query(
            value = """
    SELECT
      p.places_id          AS placesId,
      p.place_name         AS placeName,
      p.place_image_url    AS placeImageUrl,
      p.one_line_desc      AS oneLineDesc,
      p.place_lot_address  AS placeLotAddress,
      p.place_road_address AS placeRoadAddress,
      p.place_type         AS placeType,
      p.like_count         AS likeCount,
      p.view_count         AS viewCount,
      p.created_at         AS createdAt
    FROM places p
    WHERE
      (:q IS NULL OR :q = '' OR
        LOWER(p.place_name) LIKE CONCAT('%', LOWER(:q), '%') OR
        LOWER(p.place_lot_address) LIKE CONCAT('%', LOWER(:q), '%') OR
        LOWER(p.place_road_address) LIKE CONCAT('%', LOWER(:q), '%')
      )
      AND (:regionEmpty = TRUE OR p.place_region IN (:region))
      AND (:typeEmpty   = TRUE OR p.place_type   IN (:type))
    ORDER BY p.created_at DESC
  """,
            countQuery = """
    SELECT COUNT(*)
    FROM places p
    WHERE
      (:q IS NULL OR :q = '' OR
        LOWER(p.place_name) LIKE CONCAT('%', LOWER(:q), '%') OR
        LOWER(p.place_lot_address) LIKE CONCAT('%', LOWER(:q), '%') OR
        LOWER(p.place_road_address) LIKE CONCAT('%', LOWER(:q), '%')
      )
      AND (:regionEmpty = TRUE OR p.place_region IN (:region))
      AND (:typeEmpty   = TRUE OR p.place_type   IN (:type))
  """,
            nativeQuery = true
    )
    Page<PlacesFindResponseProjection> searchPlacesNative(
            @Param("q") String q,
            @Param("region") List<String> region,
            @Param("regionEmpty") boolean regionEmpty,
            @Param("type") List<String> type,
            @Param("typeEmpty") boolean typeEmpty,
            Pageable pageable
    );




}
