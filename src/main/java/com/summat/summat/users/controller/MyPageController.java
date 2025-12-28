package com.summat.summat.users.controller;

import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.mypage.PasswordChangeReqDto;
import com.summat.summat.users.dto.mypage.ProfileResDto;
import com.summat.summat.users.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/me")
    public HashMap<String, Object> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ProfileResDto getMyProfile = myPageService.getMyProfile(userDetails);

        HashMap<String, Object> result = new HashMap<>();

        result.put("status", getMyProfile != null ? 200 : 500);
        result.put("message", getMyProfile != null ? "sucess my profile search" : "fail my profile search");
        result.put("data", getMyProfile);

        return result;
    }

    @PutMapping("/pw-change")
    public HashMap<String, Object> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody PasswordChangeReqDto passwordChangeReqDto) {

        boolean isChangePassword = myPageService.changePassword(userDetails, passwordChangeReqDto);

        HashMap<String, Object> result = new HashMap<>();

        result.put("status", isChangePassword ? 200 : 500);
        result.put("message", isChangePassword ? "sucess password change" : "fail password change");
        result.put("data", isChangePassword);

        return result;
    }
}
