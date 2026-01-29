package com.summat.summat.users.service;

import com.summat.summat.auth.service.EmailVerificationService;
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

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public boolean signUp(UsersReqDto usersReqDto) {
            log.info("UsersService signUp usersReqDto = " + usersReqDto);

        if (usersReqDto.getEmail() == null || usersReqDto.getUserPw() == null || usersReqDto.getUserNickName() == null) return false;

        // 1) signupToken 검증 (DB email_verification 기준)
        boolean ok = emailVerificationService.validateSignupToken(
                usersReqDto.getEmail(),
                usersReqDto.getSignupToken()
        );
        if (!ok) return false;

        Users user = new Users();
        user.setEmail(usersReqDto.getEmail());
        user.setUserPw(passwordEncoder.encode(usersReqDto.getUserPw()));
        user.setUserNickName(usersReqDto.getUserNickName());
        user.setRole(RoleType.ROLE_USER);

        // 2) 인증 완료로 기록
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());

        usersRepository.save(user);

        // 3) 토큰 1회성 소비 처리(재사용 방지)
        emailVerificationService.consumeSignupToken(usersReqDto.getEmail());

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

    public boolean checkCurrentEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}
