package com.summat.summat.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    // 1시간 (Access)
    private final long accessTokenValidityInMillis;
    // 7일 (Refresh)
    private final long refreshTokenValidityInMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-validity-ms:3600000}") long accessTokenValidityInMillis,
            @Value("${jwt.refresh-validity-ms:604800000}") long refreshTokenValidityInMillis
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMillis = accessTokenValidityInMillis;
        this.refreshTokenValidityInMillis = refreshTokenValidityInMillis;
    }

    // Access Token 생성
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenValidityInMillis);
    }

    // Refresh Token 생성
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenValidityInMillis);
    }

    private String generateToken(UserDetails userDetails, long validityMillis) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + validityMillis);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // userId/email 등
                .claim("roles", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token).getBody();
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 만료/조작 시 여기서 예외
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT: {}", e.getMessage());
        }
        return false;
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}