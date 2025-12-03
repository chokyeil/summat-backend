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

    @Column(name = "place_name")
    private String placeName;

    @Column(name ="place_detail_address")
    private String placeDetailAddress;

//    private String place_image;

    @Column(name = "one_line_desc")
    private String oneLineDesc;

    @Column(name = "place_type")
    private String placeType;

    @Column(name = "place_region")
    private String placeRegion;

    @Column(name = "like_count")
    private long likeCount;

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
