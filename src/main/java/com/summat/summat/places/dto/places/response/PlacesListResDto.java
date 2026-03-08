package com.summat.summat.places.dto.places.response;

import lombok.Getter;

@Getter
public class PlacesListResDto {
    private String placeName;

    private String placeDetailAddress;

    private String imageUrl;

    private String summary;

    private String category;

    private String region;

    private long likeCount;

    private long viewCount;
}
