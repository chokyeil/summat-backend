package com.summat.summat.places.repository.dto;

import java.time.Instant;

public interface PlacesFindResponseProjection {
    Long getPlacesId();
    String getPlaceName();
    String getPlaceImageUrl();
    String getOneLineDesc();
    String getPlaceLotAddress();
    String getPlaceRoadAddress();
    String getPlaceType();
    Long getLikeCount();
    Long getViewCount();
    Instant getCreatedAt();
}
