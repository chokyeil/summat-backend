package com.summat.summat.places.dto.places;

import com.summat.summat.enums.PlaceTagType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
}
