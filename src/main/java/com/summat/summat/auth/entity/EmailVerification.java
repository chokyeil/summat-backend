package com.summat.summat.auth.entity;

import com.summat.summat.auth.VerificationPurpose;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "email_verification",
        indexes = {
                @Index(name = "idx_email_purpose_created", columnList = "email,purpose,createdAt")
        })
@Getter @Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EmailVerification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationPurpose purpose;

    // 코드 평문 저장 금지
    @Column(nullable = false, length = 100)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant verifiedAt;

    @Column(nullable = false)
    private int attemptCount = 0;

    @Column(nullable = false)
    private int sendCount = 0;

    @Column
    private Instant lastSentAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}

