package com.summat.summat.places.dto.places.response;

import com.summat.summat.enums.PlaceTagType;
import com.summat.summat.places.repository.dto.PlacesFindResponseProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
//@AllArgsConstructor
public class PlaceMainListResDto {
    private Long placeId;
    private String placeName;
    private String placeLotAddress;
    private String placeRoadAddress;
    private String placeImageUrl;
    private String oneLineDesc;
    private List<PlaceTagType> tags;
    private long likeCount;
    private long viewCount;
    private Instant createdAt;

//    private PlacesFindResponseDto placesFindResponseDto;
//    private PlacesFindResponseProjection placesFindResponseProjection;
//    private List<PlaceTagType> tags;
}
