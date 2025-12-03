package com.summat.summat.places.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.summat.summat.users.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Places {
    @Id
    @GeneratedValue
    @Column(name = "places_id")
    private Long id;

    // 가게 이름
    @Column(name = "place_name")
    private String placeName;

    // 가게 상세 주소
    @Column(name ="place_detail_address")
    private String placeDetailAddress;

//    private String place_image;

    // 한줄 요약
    @Column(name = "one_line_desc")
    private String oneLineDesc;

    // 업종(ex 카페/빵집/식당)
    @Column(name = "place_type")
    private String placeType;

    // 지역(ex 서울/경기/충남 등등 전국)
    @Column(name = "place_region")
    private String placeRegion;

    // 태그(WIFI//콘센트/주차가능/반려동물동반/키즈존/감성카페/북카페/작업하기좋은/조용한카페/브런치/빵집/디저트맛집/뷰맛집/포토존/테라스
    @Column(name = "tag")
    private String tag;

    // 좋아요 갯수
    @Column(name = "like_count")
    private long likeCount;

    // 조회수 갯수
    @Column(name = "view_count")
    private long viewCount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    @JsonIgnore
    private Users users;

}
