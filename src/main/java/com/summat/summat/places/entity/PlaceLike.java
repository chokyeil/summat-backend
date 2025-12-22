package com.summat.summat.places.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class PlaceLike {
    @Id
    @GeneratedValue
    @Column(name = "place_like_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "place_id")
    private Long placeId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
