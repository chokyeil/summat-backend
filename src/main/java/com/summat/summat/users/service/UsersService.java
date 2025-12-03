package com.summat.summat.users.service;

import com.summat.summat.enums.RoleType;
import com.summat.summat.users.dto.UsersReqDto;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean signUp(UsersReqDto usersReqDto) {
            log.info("UsersService signUp usersReqDto = " + usersReqDto);

            if(usersReqDto.getUserId() == null || usersReqDto.getUserPw() == null || usersReqDto.getUserNickName() == null) return false;

            Users user = new Users();
            user.setUserId(usersReqDto.getUserId());
//            user.setUserPw(usersReqDto.getUserPw());
            user.setUserPw(passwordEncoder.encode(usersReqDto.getUserPw()));
            user.setUserNickName(usersReqDto.getUserNickName());
            user.setRole(RoleType.ROLE_USER);

            usersRepository.save(user);
            return true;
    }
}
