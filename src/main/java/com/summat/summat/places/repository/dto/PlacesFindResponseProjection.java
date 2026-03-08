package com.summat.summat.places.repository.dto;

import java.time.Instant;

public interface PlacesFindResponseProjection {
    Long getPlacesId();
    String getPlaceName();
    String getImageUrl();
    String getSummary();
    String getLotAddress();
    String getRoadAddress();
    String getCategory();
    Long getLikeCount();
    Long getViewCount();
    Instant getCreatedAt();
}
