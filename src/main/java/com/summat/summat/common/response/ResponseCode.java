package com.summat.summat.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    SIGNUP_SUCCESS(201, HttpStatus.CREATED, "회원가입 완료 했습니다."),
    SIGNUP_FAIL(400, HttpStatus.BAD_REQUEST, "회원가입 실패 했습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode (int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
