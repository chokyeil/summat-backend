package com.summat.summat.users.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersReqDto {
    private String userId;
    private String userPw;
    private String userNickName;
}
