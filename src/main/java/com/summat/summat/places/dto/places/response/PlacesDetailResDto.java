package com.summat.summat.places.dto.places.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacesDetailResDto {

    private String placeName;

    // 지번 주소 (선택 or 필수는 정책에 따라)
    private String placeLotAddress;

    // 도로명 주소
    private String placeRoadAddress;

    // 한 줄 설명
    private String oneLineDesc;

    // 상세 설명
    private String placeDescription;

    // 업종 (카페/식당/빵집 등)
    private String placeType;

    // 지역 (서울/부산 등)
    private String placeRegion;

    private long likeCount;

    private long viewCount;

}
