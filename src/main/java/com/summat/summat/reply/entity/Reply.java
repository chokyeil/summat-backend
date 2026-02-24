package com.summat.summat.reply.entity;

import com.summat.summat.places.entity.Places;
import com.summat.summat.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "reply")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Reply {

    @Id
    @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id", nullable = false)
    private Places place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    @Column(nullable = false, length = 500)
    private String content;

    // 0: 댓글, 1: 대댓글
    @Column(nullable = false)
    private int depth = 0;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean hidden = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
