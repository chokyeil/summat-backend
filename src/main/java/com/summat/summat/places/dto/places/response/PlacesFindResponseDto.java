package com.summat.summat.places.dto.places.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class PlacesFindResponseDto {
           private long placesId;
           private String placeName;
           private String imageUrl;
           private String summary;
           private String lotAddress;
           private String roadAddress;
           private String category;
           private long likeCount;
           private long viewCount;
           private Instant createdAt;
}
