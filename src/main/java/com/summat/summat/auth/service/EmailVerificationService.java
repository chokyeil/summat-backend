package com.summat.summat.auth.service;

import com.summat.summat.auth.VerificationPurpose;
import com.summat.summat.auth.entity.EmailVerification;
import com.summat.summat.auth.repository.EmailVerificationRepository;
import com.summat.summat.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // BCrypt 사용 권장
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.mail.username}")
    private String fromAddress;

    private final SecureRandom random = new SecureRandom();

    private final int codeTtlMinutes = 10;
    private final int maxAttempts = 5;
    private final int cooldownSeconds = 60;

    @Transactional
    public void sendCode(String email, VerificationPurpose purpose) {
        Instant now = Instant.now();

        repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose).ifPresent(latest -> {
            if (latest.getLastSentAt() != null &&
                    latest.getLastSentAt().plusSeconds(cooldownSeconds).isAfter(now)) {
                throw new RuntimeException("요청이 너무 잦습니다. 잠시 후 다시 시도해주세요.");
            }
        });

        String code = generate6Digits();
        String codeHash = passwordEncoder.encode(code);

        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setPurpose(purpose);
        ev.setCodeHash(codeHash);
        ev.setExpiresAt(now.plusSeconds(codeTtlMinutes * 60L));
        ev.setSendCount(1);
        ev.setLastSentAt(now);

        repository.save(ev);

        log.info("[DEV][EMAIL-OTP] email={}, purpose={}, code={}", email, purpose, code);

        // 메일 발송
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[숨맛] 이메일 인증번호");
            helper.setText(
                    "인증번호: " + code + "\n유효시간: " + codeTtlMinutes + "분",
                    false
            );

            // 주소는 그대로, 이름만 변경
            helper.setFrom(new InternetAddress(fromAddress, "숨맛 인증센터"));

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("[EMAIL-SEND-FAIL] to={}, reason={}", email, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    @Transactional
    public String verifyCodeAndIssueToken(String email, String code, VerificationPurpose purpose) {
        Instant now = Instant.now();

        EmailVerification ev = repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new RuntimeException("인증 요청이 없습니다."));

        if (ev.isVerified()) {
            // 이미 인증됨: 토큰 재발급해도 됨(정책)
            return jwtTokenProvider.createSignupToken(email, purpose);
        }

        if (ev.isExpired(now)) {
            throw new RuntimeException("인증번호가 만료되었습니다.");
        }

        if (ev.getAttemptCount() >= maxAttempts) {
            throw new RuntimeException("인증 시도 횟수를 초과했습니다.");
        }

        ev.setAttemptCount(ev.getAttemptCount() + 1);

        if (!passwordEncoder.matches(code, ev.getCodeHash())) {
            repository.save(ev);
            throw new RuntimeException("인증번호가 올바르지 않습니다.");
        }

        // OTP 검증 성공
        ev.setVerifiedAt(now);

        // signupToken 발급 + DB 저장
        String signupToken = jwtTokenProvider.createSignupToken(email, purpose);
        ev.setSignupToken(signupToken);
        ev.setSignupTokenExpiresAt(now.plusSeconds(10 * 60)); // 10분
        ev.setConsumedAt(null);

        repository.save(ev);
        return signupToken;
    }

    private String generate6Digits() {
        int n = random.nextInt(900000) + 100000;
        return String.valueOf(n);
    }

    @Transactional(readOnly = true)
    public boolean validateSignupToken(String email, String signupToken) {
        if (signupToken == null || signupToken.isBlank()) return false;

        EmailVerification ev = repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(
                email, VerificationPurpose.SIGNUP
        ).orElse(null);

        if (ev == null) return false;

        Instant now = Instant.now();

        // 1) OTP 인증 완료 여부
        if (ev.getVerifiedAt() == null) return false;

        // 2) signupToken 만료 여부
        if (ev.getSignupTokenExpiresAt() == null || ev.getSignupTokenExpiresAt().isBefore(now)) return false;

        // 3) 1회성 소비 여부
        if (ev.getConsumedAt() != null) return false;

        // 4) 토큰 해시 일치 여부
        if (ev.getSignupToken() == null) return false;

        return signupToken.equals(ev.getSignupToken());

    }

    @Transactional
    public void consumeSignupToken(String email) {
        EmailVerification ev = repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(
                email, VerificationPurpose.SIGNUP
        ).orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        ev.setConsumedAt(Instant.now());
        repository.save(ev);
    }
}

