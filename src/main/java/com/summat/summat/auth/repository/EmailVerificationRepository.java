package com.summat.summat.auth.repository;

import com.summat.summat.auth.VerificationPurpose;
import com.summat.summat.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 최신 1건 가져오기 (인증번호는 최신만 유효하게)
    @Query("""
        select ev from EmailVerification ev
        where ev.email = :email and ev.purpose = :purpose
        order by ev.createdAt desc
    """)
    Optional<EmailVerification> findLatest(@Param("email") String email,
                                           @Param("purpose") VerificationPurpose purpose);
}

