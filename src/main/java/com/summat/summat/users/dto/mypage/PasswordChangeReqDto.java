package com.summat.summat.users.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeReqDto {
    // 변경할 비밀번호
    private String newPassword;

    // 변경할 비밀번호 재입력
    private String newPasswordConfirm;
}
