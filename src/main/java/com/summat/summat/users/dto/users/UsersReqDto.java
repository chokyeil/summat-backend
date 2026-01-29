package com.summat.summat.users.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UsersReqDto {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입니다.")
    private String email;

    @Size(min = 8, max = 20, message = "비밀번호는 최소8자 부터 최대 20자 까지 입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입니다.")
    private String userPw;

    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]+$",
            message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.")
    private String userNickName;

    @NotBlank(message = "인증 토큰이 필요합니다.")
    private String signupToken;
}
