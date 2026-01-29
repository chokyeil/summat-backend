package com.summat.summat.auth.repository;

import com.summat.summat.auth.VerificationPurpose;
import com.summat.summat.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            VerificationPurpose purpose
    );
}

