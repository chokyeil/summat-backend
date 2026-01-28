package com.summat.summat.auth.controller;

import com.summat.summat.auth.VerificationPurpose;
import com.summat.summat.auth.dto.EmailSendReq;
import com.summat.summat.auth.dto.EmailVerifyReq;
import com.summat.summat.auth.service.EmailVerificationService;
import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> send(@RequestBody @Valid EmailSendReq req) {
        VerificationPurpose purpose = VerificationPurpose.valueOf(req.getPurpose());
        emailVerificationService.sendCode(req.getEmail(), purpose);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.EMAIL_SEND_SUCCESS, null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verify(@RequestBody @Valid EmailVerifyReq req) {
        VerificationPurpose purpose = VerificationPurpose.valueOf(req.getPurpose());
        String signupToken = emailVerificationService.verifyCodeAndIssueToken(req.getEmail(), req.getCode(), purpose);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.EMAIL_VERIFY_SUCCESS, signupToken));
    }
}

