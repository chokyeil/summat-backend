package com.summat.summat.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class EmailVerifyReq {
    @Email(message = "올바른 이메일 형식이 아닙니다.") @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank
    private String code; // 6자리

    @NotBlank
    private String purpose;
}
