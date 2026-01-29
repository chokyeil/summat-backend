package com.summat.summat.config.security.jwt;

import com.summat.summat.auth.VerificationPurpose;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
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
    // 30분 (Signup)
    private final long signupTokenValidityInMillis;

    // claims key
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_PURPOSE = "purpose";

    // token types
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";
    private static final String TYPE_SIGNUP = "SIGNUP";

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-validity-ms:3600000}") long accessTokenValidityInMillis,
            @Value("${jwt.refresh-validity-ms:604800000}") long refreshTokenValidityInMillis,
            @Value("${jwt.signup-validity-ms:1800000}") long signupTokenValidityInMillis
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMillis = accessTokenValidityInMillis;
        this.refreshTokenValidityInMillis = refreshTokenValidityInMillis;
        this.signupTokenValidityInMillis = signupTokenValidityInMillis;
    }

    /* =========================
     * Access / Refresh Tokens
     * ========================= */

    // Access Token 생성
    public String generateAccessToken(UserDetails userDetails) {
        return generateAuthToken(userDetails, accessTokenValidityInMillis, TYPE_ACCESS);
    }

    // Refresh Token 생성
    public String generateRefreshToken(UserDetails userDetails) {
        return generateAuthToken(userDetails, refreshTokenValidityInMillis, TYPE_REFRESH);
    }

    private String generateAuthToken(UserDetails userDetails, long validityMillis, String tokenType) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + validityMillis);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // 여기엔 email을 넣는 걸 추천(이미 email로 전환 중)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Access/Refresh에서 subject(=username/email) 꺼내기 */
    public String getUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    /** Access/Refresh roles 꺼내기 */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token).getBody();
        return claims.get(CLAIM_ROLES, List.class);
    }

    /* =========================
     * Signup Token
     * ========================= */

    /**
     * 이메일 인증 완료 후 회원가입에 사용할 signupToken 발급
     * - subject는 굳이 고정 문자열로 둘 필요 없음(여기서는 email을 subject로 사용)
     * - tokenType=SIGNUP, purpose=SIGNUP(or RESET_PASSWORD 같은 용도), email 포함
     */
    public String createSignupToken(String email, VerificationPurpose purpose) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(signupTokenValidityInMillis);

        return Jwts.builder()
                .setSubject(email) // 토큰 주체를 email로 (추후 검증/매칭이 가장 단순)
                .claim(CLAIM_TOKEN_TYPE, TYPE_SIGNUP)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_PURPOSE, purpose.name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** signupToken 검증 후 payload 파싱 */
    public SignupTokenPayload validateAndParseSignupToken(String token) {
        try {
            Claims claims = parseClaims(token).getBody();

            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!TYPE_SIGNUP.equals(tokenType)) {
                throw new IllegalArgumentException("Not a signup token");
            }

            String email = claims.get(CLAIM_EMAIL, String.class);
            String purposeStr = claims.get(CLAIM_PURPOSE, String.class);
            VerificationPurpose purpose = VerificationPurpose.valueOf(purposeStr);

            // subject=email로 썼으니 일치 체크(안전장치)
            String subject = claims.getSubject();
            if (subject == null || !subject.equals(email)) {
                throw new IllegalArgumentException("Signup token subject mismatch");
            }

            return new SignupTokenPayload(email, purpose);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Signup token expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid signup token", e);
        }
    }

    public record SignupTokenPayload(String email, VerificationPurpose purpose) {}

    /* =========================
     * Common
     * ========================= */

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

    /** 토큰 타입 조회(ACCESS/REFRESH/SIGNUP) */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token).getBody();
        return claims.get(CLAIM_TOKEN_TYPE, String.class);
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}