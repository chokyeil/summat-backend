package com.summat.summat.admin.dto;

import com.summat.summat.enums.RoleType;
import com.summat.summat.enums.UserStatus;
import com.summat.summat.users.entity.Users;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AdminUserDetailResDto {

    private Long id;
    private String email;
    private String nickName;
    private RoleType role;
    private UserStatus status;
    private boolean emailVerified;
    private Instant emailVerifiedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public AdminUserDetailResDto(Users user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getUserNickName();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.emailVerified = user.isEmailVerified();
        this.emailVerifiedAt = user.getEmailVerifiedAt();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
