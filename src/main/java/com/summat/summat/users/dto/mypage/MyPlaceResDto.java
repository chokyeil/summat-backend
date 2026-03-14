package com.summat.summat.users.dto.mypage;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MyPlaceResDto {
    private Long placeId;
    private String placeName;
    private String imageUrl;
    private String summary;
    private String category;
    private String region;
    private Instant createdAt;
}
