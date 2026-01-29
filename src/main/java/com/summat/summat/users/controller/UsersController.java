package com.summat.summat.users.controller;

import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.users.PasswordCheckReqDto;
import com.summat.summat.users.dto.users.UsersReqDto;
import com.summat.summat.users.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/summatUsers")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/id-check")
    public ResponseEntity<ApiResponse> checkCurrentUserId(@RequestParam String email) {
        boolean isUserId = usersService.checkCurrentEmail(email);

        return ResponseEntity.status(!isUserId ? ResponseCode.USERID_AVAILABLE.getHttpStatus() : ResponseCode.USERID_DUPLICATED.getHttpStatus())
                .body(!isUserId ? new ApiResponse(ResponseCode.USERID_AVAILABLE, null) : new ApiResponse(ResponseCode.USERID_DUPLICATED, null));
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@RequestBody @Valid UsersReqDto usersReqDto) {
        log.info("UsersController signUp getEmail = " + usersReqDto.getEmail());
        log.info("UsersController signUp userPw = " + usersReqDto.getUserPw());
        log.info("UsersController signUp userNickName = " + usersReqDto.getUserNickName());

        boolean saveResult = usersService.signUp(usersReqDto);
        log.info("UsersController signUp saveResult = " + saveResult);

        return ResponseEntity
                .status(saveResult ? ResponseCode.SIGNUP_SUCCESS.getHttpStatus() : ResponseCode.SIGNUP_FAIL.getHttpStatus())
                .body(saveResult ? new ApiResponse<>(ResponseCode.SIGNUP_SUCCESS, null) : new ApiResponse<>(ResponseCode.SIGNUP_FAIL, null));
    }

    @PostMapping("/pw-check")
    public ResponseEntity<ApiResponse> checkCurrentPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestBody PasswordCheckReqDto passwordCheckReqDto) {

        boolean isCurrentPassword = usersService.checkCurrentPassword(userDetails, passwordCheckReqDto);

        return ResponseEntity.status(isCurrentPassword ? ResponseCode.SIGNUP_SUCCESS.getHttpStatus() : ResponseCode.PASSWORD_MISMATCH.getHttpStatus())
                .body(isCurrentPassword ? new ApiResponse<>(ResponseCode.PASSWORD_MATCHED, null) : new ApiResponse(ResponseCode.PASSWORD_MISMATCH, null));
    }

    @GetMapping("/nick-name-check")
    public ResponseEntity<ApiResponse> checkCurrentNickName(@RequestParam String checkNickName) {
        boolean isNickName = usersService.checkCurrentNickName(checkNickName);

        return ResponseEntity.status(!isNickName ? ResponseCode.NICKNAME_AVAILABLE.getHttpStatus() : ResponseCode.NICKNAME_DUPLICATED.getHttpStatus())
                .body(!isNickName ? new ApiResponse(ResponseCode.NICKNAME_AVAILABLE, null) : new ApiResponse(ResponseCode.NICKNAME_DUPLICATED, null));
    }
}
