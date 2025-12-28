package com.summat.summat.users.dto.mypage;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProfileResDto {
    private String userId;
    private String nickName;
    private Instant createdAt;
}
