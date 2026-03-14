package com.summat.summat.users.dto.mypage;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MyLikedPlaceResDto {
    private Long placeId;
    private String placeName;
    private String imageUrl;
    private String summary;
    private String category;
    private String region;
    private boolean liked;
    private long likeCount;
    private Instant createdAt;
}
