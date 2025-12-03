package com.summat.summat.users.controller;

import com.summat.summat.users.dto.UsersReqDto;
import com.summat.summat.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/summatUsers")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/signup")
    public HashMap<String, Object> signUp(@RequestBody UsersReqDto usersReqDto) {
        log.info("UsersController signUp userId = " + usersReqDto.getUserId());
        log.info("UsersController signUp userPw = " + usersReqDto.getUserPw());
        log.info("UsersController signUp userNickName = " + usersReqDto.getUserNickName());

        boolean saveResult = usersService.signUp(usersReqDto);
        log.info("UsersController signUp saveResult = " + saveResult);
        HashMap<String, Object> signUpResponse = new HashMap<>();

        signUpResponse.put("status", saveResult == true ? 200 : 500);
        signUpResponse.put("messagw", saveResult == true ? "회원가입 완료 했습니다." : "회원가입 실패 했습니다.");

        return signUpResponse;
    }
}
