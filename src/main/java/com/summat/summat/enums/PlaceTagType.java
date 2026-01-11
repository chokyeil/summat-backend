package com.summat.summat.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PlaceTagType {
    WIFI("wifi", "와이파이"),
    SOCKET("socket", "콘센트"),
    PARKING("parking", "주차가능"),
    PET("pet", "반려동물동반"),
    KIDS("kids", "키즈존"),

    MOOD("mood", "감성카페"),
    BOOK("book", "북카페"),
    WORK("work", "작업하기좋은"),
    QUIET("quiet", "조용한카페"),

    BRUNCH("brunch", "브런치"),
    BAKERY("bakery", "빵집"),
    DESSERT("dessert", "디저트맛집"),

    VIEW("view", "뷰맛집"),
    PHOTO("photo", "포토존"),
    TERRACE("terrace", "테라스");

    private final String code;   // API에서 쓰는 값
    private final String label;  // 화면 표시용

    PlaceTagType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static PlaceTagType fromCode(String code) {
        for (PlaceTagType tag : values()) {
            if (tag.code.equalsIgnoreCase(code)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid tag code: " + code);
    }

    @JsonValue
    public String jsonValue() {
        return code; // "wifi"
    }
}
