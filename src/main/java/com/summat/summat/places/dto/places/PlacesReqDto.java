package com.summat.summat.places.dto.places;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

 import java.util.List;

@Getter
@Setter
public class PlacesReqDto {
    @NotBlank(message = "가게 이름은 필수입니다.")
    @Size(max = 50, message = "가게 이름은 최대 50자까지 가능합니다.")
    private String placeName;

    // 지번 주소 (선택 or 필수는 정책에 따라)
    @NotBlank(message = "지번 주소는 필수입니다.")
    @Size(max = 100, message = "지번 주소는 최대 100자까지 가능합니다.")
    private String placeLotAddress;

    // 도로명 주소
    @NotBlank(message = "도로명 주소는 필수입니다.")
    @Size(max = 100, message = "도로명 주소는 최대 100자까지 가능합니다.")
    private String placeRoadAddress;

    // 한 줄 설명
    @Size(max = 50, message = "한 줄 설명은 최대 50자까지 가능합니다.")
    private String oneLineDesc;

    // 상세 설명
    @Size(max = 500, message = "가게 설명은 최대 500자까지 가능합니다.")
    private String placeDescription;

    // 업종 (카페/식당/빵집 등)
//    @NotBlank(message = "업종은 필수입니다.")
    private String placeType;

    // 지역 (서울/부산 등)
//    @NotBlank(message = "지역은 필수입니다.")
    private String placeRegion;

    private List<String> tags;

}
