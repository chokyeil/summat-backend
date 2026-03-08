package com.summat.summat.places.dto.places.response;

import com.summat.summat.enums.PlaceTagType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PlacesDetailResDto {

    private Long placeId;

    private String placeName;

    private String imageUrl;

    // 지번 주소 (선택 or 필수는 정책에 따라)
    private String lotAddress;

    // 도로명 주소
    private String roadAddress;

    // 한 줄 설명
    private String summary;

    // 상세 설명
    private String description;

    // 업종 (카페/식당/빵집 등)
    private String category;

    // 지역 (서울/부산 등)
    private String region;

    private List<PlaceTagType> tags;

    private long likeCount;

    private long viewCount;

    private Instant createdAt;

}
