package com.summat.summat.users.controller;

import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.mypage.MyPlaceResDto;
import com.summat.summat.users.dto.mypage.PasswordChangeReqDto;
import com.summat.summat.users.dto.mypage.ProfileResDto;
import com.summat.summat.users.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProfileResDto result = myPageService.getMyProfile(userDetails);
        return ResponseEntity.status(ResponseCode.PROFILE_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.PROFILE_SUCCESS, result));
    }

    @GetMapping("/places")
    public ResponseEntity<ApiResponse> getMyPlaces(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MyPlaceResDto> result = myPageService.getMyPlaces(userDetails);
        return ResponseEntity.status(ResponseCode.PLACE_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.PLACE_LIST_SUCCESS, result));
    }

    @PutMapping("/pw-change")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordChangeReqDto passwordChangeReqDto) {
        boolean isChanged = myPageService.changePassword(userDetails, passwordChangeReqDto);
        ResponseCode code = isChanged ? ResponseCode.PASSWORD_CHANGED : ResponseCode.PASSWORD_CHANGE_FAILED;
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ApiResponse<>(code, null));
    }
}
