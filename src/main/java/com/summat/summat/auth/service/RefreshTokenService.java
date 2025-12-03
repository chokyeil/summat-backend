package com.summat.summat.auth.service;

import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UsersRepository usersRepository;

    // 로그인 시 Refresh Token 저장/업데이트
    public void saveRefreshToken(String userId, String refreshToken, Instant expiry) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(expiry);
        usersRepository.save(user);
    }

    // 재발급 요청 시 Refresh Token 검증
    public boolean validateRefreshToken(String userId, String refreshToken) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getRefreshToken() == null) return false;
        if (!user.getRefreshToken().equals(refreshToken)) return false;
        if (user.getRefreshTokenExpiry() == null) return false;
        if (user.getRefreshTokenExpiry().isBefore(Instant.now())) return false;

        return true;
    }

    // 로그아웃 시 Refresh Token 제거
    public void clearRefreshToken(String userId) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        usersRepository.save(user);
    }
}

