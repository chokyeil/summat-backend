package com.summat.summat.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.summat.summat.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    Long id;

    @Column(name = "id", nullable = false, length = 100)
    @NotNull
    String userId;

    @Column(name = "password", nullable = false, length = 100)
    @JsonIgnore
    @NotNull
    String userPw;

    @Column(name = "nickName", nullable = false, length = 40)
    @NotNull
    String userNickName;

    @Column(nullable = false, length = 20)
    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleType role = RoleType.ROLE_USER;

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
