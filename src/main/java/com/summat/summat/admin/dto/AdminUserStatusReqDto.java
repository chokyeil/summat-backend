package com.summat.summat.admin.dto;

import com.summat.summat.enums.UserStatus;
import lombok.Getter;

@Getter
public class AdminUserStatusReqDto {
    private UserStatus status;
}
