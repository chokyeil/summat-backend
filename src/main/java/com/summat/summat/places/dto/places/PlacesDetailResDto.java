package com.summat.summat.places.dto.places;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacesDetailResDto {
    private String placeName;

    private String placeDetailAddress;

    private String placeDescription;

    private String placeImageUrl;

    private String oneLineDesc;

    private String placeType;

    private String placeRegion;

    private long likeCount;

    private long viewCount;
}
