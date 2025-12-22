package com.summat.summat.places.dto.places;

import lombok.Data;
import lombok.Getter;

@Getter
public class PlacesListResDto {
    private String placeName;

    private String placeDetailAddress;

    private String placeImageUrl;

    private String oneLineDesc;

    private String placeType;

    private String placeRegion;

    private long likeCount;

    private long viewCount;
}
