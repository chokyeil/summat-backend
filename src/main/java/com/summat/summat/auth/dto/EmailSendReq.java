package com.summat.summat.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailSendReq {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String purpose; // "SIGNUP" / "RESET_PASSWORD"
}
