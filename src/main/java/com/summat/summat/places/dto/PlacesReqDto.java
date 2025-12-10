package com.summat.summat.places.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacesReqDto {
    private String placeName;

    private String placeDetailAddress;

    private String oneLineDesc;

    private String placeDescription;

    private String placeType;

    private String placeRegion;

    private long likeCount;

    private long viewCount;
}
