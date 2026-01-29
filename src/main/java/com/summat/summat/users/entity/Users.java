package com.summat.summat.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.summat.summat.enums.RoleType;
import jakarta.persistence.*;
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
public class Users {
    @Id
    @GeneratedValue
    @Column(name = "users_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    @JsonIgnore
    private String userPw;

    @Column(name = "nickName", nullable = false, length = 8, unique = true)
    private String userNickName;

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private RoleType role = RoleType.ROLE_USER;

    // 이메일 인증 상태
    @Column(nullable = false)
    private boolean emailVerified;

    @Column
    private Instant emailVerifiedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private Instant refreshTokenExpiry;
}
