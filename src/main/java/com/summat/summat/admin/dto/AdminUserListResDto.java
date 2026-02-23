package com.summat.summat.admin.dto;

import com.summat.summat.enums.RoleType;
import com.summat.summat.enums.UserStatus;
import com.summat.summat.users.entity.Users;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AdminUserListResDto {

    private Long id;
    private String email;
    private String nickName;
    private RoleType role;
    private UserStatus status;
    private boolean emailVerified;
    private Instant createdAt;

    public AdminUserListResDto(Users user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getUserNickName();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.emailVerified = user.isEmailVerified();
        this.createdAt = user.getCreatedAt();
    }
}
