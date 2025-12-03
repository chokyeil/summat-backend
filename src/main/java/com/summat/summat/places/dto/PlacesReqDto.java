package com.summat.summat.places.dto;

import lombok.Data;

@Data
public class PlacesReqDto {
    private String placeName;

    private String placeDetailAddress;

//    private String place_image;

    private String oneLineDesc;

    private String placeType;

    private String placeRegion;

    private long likeCount;

    private long viewCount;
}
