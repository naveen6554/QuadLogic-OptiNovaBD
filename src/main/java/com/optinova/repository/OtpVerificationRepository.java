package com.optinova.repository;

import com.optinova.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for OtpVerification entity lookup.
 */
@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, String purpose);

    Optional<OtpVerification> findByEmailAndOtpCodeAndPurposeAndUsedFalseAndExpiryDateAfter(
            String email, String otpCode, String purpose, LocalDateTime now);
}
