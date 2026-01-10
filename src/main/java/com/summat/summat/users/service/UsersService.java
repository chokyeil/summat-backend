package com.summat.summat.users.service;

import com.summat.summat.enums.RoleType;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.users.PasswordCheckReqDto;
import com.summat.summat.users.dto.users.UsersReqDto;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public boolean checkCurrentPassword(CustomUserDetails userDetails, PasswordCheckReqDto passwordCheckReqDto) {
        String currentPassword = passwordCheckReqDto.getCurrentPassword();
        Long userId = userDetails.getUser().getId();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        log.info("currentPassword encoding = " + passwordEncoder.encode(currentPassword));
        log.info("findPassword encoding = " + user.getUserPw());


        return passwordEncoder.matches(currentPassword, user.getUserPw());


    }

    public boolean checkCurrentNickName(String checkNickName) {
        return usersRepository.existsByUserNickName(checkNickName);
    }

    public boolean checkCurrentUserId(String userId) {
        return usersRepository.existsByUserId(userId);
    }
}
