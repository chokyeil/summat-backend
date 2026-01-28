package com.summat.summat.auth.token;

import com.summat.summat.auth.VerificationPurpose;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class SignupTokenProvider {

    private final Key key;
    private final long signupTokenTtlSeconds = 30 * 60; // 30분

    public SignupTokenProvider(/* application.yml에서 secret 주입 */ String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createSignupToken(String email, VerificationPurpose purpose) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(signupTokenTtlSeconds);

        return Jwts.builder()
                .setSubject("signup-token")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim(SignupTokenClaims.TYPE, SignupTokenClaims.TYPE_SIGNUP)
                .claim(SignupTokenClaims.EMAIL, email)
                .claim(SignupTokenClaims.PURPOSE, purpose.name())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public SignupTokenPayload validateAndParse(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();

            String type = claims.get(SignupTokenClaims.TYPE, String.class);
            if (!SignupTokenClaims.TYPE_SIGNUP.equals(type)) {
                throw new IllegalArgumentException("Invalid signupToken type");
            }

            String email = claims.get(SignupTokenClaims.EMAIL, String.class);
            String purposeStr = claims.get(SignupTokenClaims.PURPOSE, String.class);

            VerificationPurpose purpose = VerificationPurpose.valueOf(purposeStr);

            return new SignupTokenPayload(email, purpose);

        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid signupToken", e);
        }
    }

    public record SignupTokenPayload(String email, VerificationPurpose purpose) {}
}

