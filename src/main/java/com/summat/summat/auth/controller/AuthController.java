package com.summat.summat.auth.controller;

import com.summat.summat.auth.dto.LoginRequest;
import com.summat.summat.auth.dto.LoginResponse;
import com.summat.summat.auth.service.RefreshTokenService;
import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.config.security.jwt.JwtTokenProvider;
import com.summat.summat.users.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    // 1) 로그인: access + refresh 발급
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPw())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Refresh 토큰 만료일 계산 (지금 + refresh validity)
        Instant refreshExpiry = Instant.now().plusSeconds(60L * 60 * 24 * 7); // 7일 예시
        refreshTokenService.saveRefreshToken(userDetails.getUsername(), refreshToken, refreshExpiry);

//        return ResponseEntity.ok(new LoginResponse(accessToken, "Bearer", refreshToken));
        return ResponseEntity.status(ResponseCode.LOGIN_SUCCESS.getHttpStatus()).body(new ApiResponse<>(ResponseCode.LOGIN_SUCCESS, new LoginResponse(accessToken, "Bearer", refreshToken)));
    }

    // 2) Refresh 토큰으로 Access 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String refreshToken = body.get("refreshToken");

        if (!refreshTokenService.validateRefreshToken(userId, refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

        // Access만 새로 발급, Refresh는 그대로 쓰는 패턴
        return ResponseEntity.ok(new LoginResponse(newAccessToken, "Bearer", refreshToken));
    }

    // 3) 로그아웃: Refresh Token 제거
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        refreshTokenService.clearRefreshToken(userId);
        // Access Token은 짧게 가져가고, 서버에서 블랙리스트까지 관리하고 싶으면
        // 별도 저장소(예: Redis)에 넣는 방식으로 확장 가능
        return ResponseEntity.ok().build();
    }
}
