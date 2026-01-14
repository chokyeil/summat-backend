package com.summat.summat.places.dto.places.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class PlacesFindResponseDto {
           private long placesId;
           private String placeName;
           private String placeImageUrl;
           private String oneLineDesc;
           private String placeLotAddress;
           private String placeRoadAddress;
           private String placeType;
           private long likeCount;
           private long viewCount;
           private Instant createdAt;
}
