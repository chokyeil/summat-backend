package com.summat.summat.users.controller;

import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.users.PasswordCheckReqDto;
import com.summat.summat.users.dto.users.UsersReqDto;
import com.summat.summat.users.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/summatUsers")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/signup")
    public HashMap<String, Object> signUp(@RequestBody @Valid UsersReqDto usersReqDto) {
        log.info("UsersController signUp userId = " + usersReqDto.getUserId());
        log.info("UsersController signUp userPw = " + usersReqDto.getUserPw());
        log.info("UsersController signUp userNickName = " + usersReqDto.getUserNickName());

        boolean saveResult = usersService.signUp(usersReqDto);
        log.info("UsersController signUp saveResult = " + saveResult);
        HashMap<String, Object> signUpResponse = new HashMap<>();

        signUpResponse.put("status", saveResult ? 200 : 500);
        signUpResponse.put("messagw", saveResult ? "회원가입 완료 했습니다." : "회원가입 실패 했습니다.");

        return signUpResponse;
    }

    @PostMapping("/pw-check")
    public HashMap<String, Object> checkCurrentPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody PasswordCheckReqDto passwordCheckReqDto) {

        boolean isCurrentPassword = usersService.checkCurrentPassword(userDetails, passwordCheckReqDto);
        HashMap<String, Object> result = new HashMap<>();

        result.put("status", isCurrentPassword ? 200 : 500);
        result.put("message", isCurrentPassword ? "true current password" : "false current password");
        result.put("data", isCurrentPassword);

        return result;
    }

    @GetMapping("/nick-name-check/{nickname}")
    public HashMap<String, Object> checkCurrentNickName(@PathVariable String checkNickName) {
        Boolean isNickName = usersService.checkCurrentNickName(checkNickName);

        HashMap<String, Object> result = new HashMap<>();

        result.put("status", isNickName ? 200 : 500);
        result.put("message", isNickName ? "found nick name" : "not found nick name");
        result.put("data", isNickName);

        return result;
    }
}
