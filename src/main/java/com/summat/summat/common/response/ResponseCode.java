package com.summat.summat.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    USERID_AVAILABLE(200, HttpStatus.OK, "사용 가능한 아이디 입니다."),
    USERID_DUPLICATED(409, HttpStatus.CONFLICT, "사용 중인 아이디 입니다."),

    SIGNUP_SUCCESS(201, HttpStatus.CREATED, "회원가입 완료 했습니다."),
    LOGIN_SUCCESS(200, HttpStatus.OK, "로그인 성공 했습니다."),
    LOGIN_FAIL(401, HttpStatus.UNAUTHORIZED, "로그인 실패 했습니다."),
    SIGNUP_FAIL(400, HttpStatus.BAD_REQUEST, "회원가입 실패 했습니다."),
    PASSWORD_MATCHED(200, HttpStatus.OK, "현재 비밀번호 일치 했습니다."),
    PASSWORD_MISMATCH(401, HttpStatus.UNAUTHORIZED,"현재 비밀번호 일치 하지 않습니다."),

    NICKNAME_AVAILABLE(200, HttpStatus.OK, "사용 가능한 닉네임 입니다."),
    NICKNAME_DUPLICATED(409, HttpStatus.CONFLICT, "사용 중인 닉네임 입니다."),

    PLACE_CREATED(201, HttpStatus.CREATED, "숨맛 장소 생성 완료 했습니다."),
    PLACE_CREATE_FAILED(400, HttpStatus.BAD_REQUEST, "숨맛 장소 생성 실패 했습니다."),
    PLACE_LIST_SUCCESS(200, HttpStatus.OK, "장소 목록 조회 성공 했습니다."),
    PLACE_UPDATED(200, HttpStatus.OK, "장소 정보가 수정되었습니다."),
    PLACE_DELETED(200, HttpStatus.OK, "장소 정보가 삭제되었습니다."),
    PLACE_DETAIL_SUCCESS(200, HttpStatus.OK, "장소 상세 조회 성공"),
    PLACE_VIEW_INCREMENTED(200, HttpStatus.OK, "조회수가 증가되었습니다."),
    PLACE_LIKE_SUCCESS(200, HttpStatus.OK, "좋아요를 눌렀습니다."),
    PLACE_ALREADY_LIKED(409, HttpStatus.CONFLICT, "이미 좋아요한 장소입니다."),
    PLACE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."),

    // Email 인증
    EMAIL_SEND_SUCCESS(200, HttpStatus.OK, "이메일 인증번호 발송 성공"),
    EMAIL_VERIFY_SUCCESS(200, HttpStatus.OK, "이메일 인증 성공");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode (int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
