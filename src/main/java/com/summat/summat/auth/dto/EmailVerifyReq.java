package com.summat.summat.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class EmailVerifyReq {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String code; // 6자리

    @NotBlank
    private String purpose;
}
