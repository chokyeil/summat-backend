package com.summat.summat.places.dto.places;

import com.summat.summat.enums.PlaceTagType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceMainListResDto {
    private Long placeId;
    private String placeName;
    private String placeRegion;
    private String placeType;
    private String placeImageUrl;
    private List<PlaceTagType> tags;
    private long likeCount;
    private long viewCount;
}
