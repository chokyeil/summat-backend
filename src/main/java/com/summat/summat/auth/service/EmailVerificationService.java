package com.summat.summat.auth.service;

import com.summat.summat.auth.VerificationPurpose;
import com.summat.summat.auth.entity.EmailVerification;
import com.summat.summat.auth.repository.EmailVerificationRepository;
import com.summat.summat.auth.token.SignupTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // BCrypt 사용 권장
    private final SignupTokenProvider signupTokenProvider;

    private final SecureRandom random = new SecureRandom();

    private final int codeTtlMinutes = 10;
    private final int maxAttempts = 5;
    private final int cooldownSeconds = 60;

    @Transactional
    public void sendCode(String email, VerificationPurpose purpose) {
        Instant now = Instant.now();

        repository.findLatest(email, purpose).ifPresent(latest -> {
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
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("[숨맛] 이메일 인증번호");
        msg.setText("인증번호: " + code + "\n유효시간: " + codeTtlMinutes + "분");
        mailSender.send(msg);
    }

    @Transactional
    public String verifyCodeAndIssueToken(String email, String code, VerificationPurpose purpose) {
        Instant now = Instant.now();

        EmailVerification ev = repository.findLatest(email, purpose)
                .orElseThrow(() -> new RuntimeException("인증 요청이 없습니다."));

        if (ev.isVerified()) {
            // 이미 인증됨: 토큰 재발급해도 됨(정책)
            return signupTokenProvider.createSignupToken(email, purpose);
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

        ev.setVerifiedAt(now);
        repository.save(ev);

        return signupTokenProvider.createSignupToken(email, purpose);
    }

    private String generate6Digits() {
        int n = random.nextInt(900000) + 100000;
        return String.valueOf(n);
    }
}

