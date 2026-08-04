package com.optinova.repository;

import com.optinova.entity.OtpVerification;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for OtpVerification entity lookup.
 */
// @Repository
public interface OtpVerificationRepository {
// Deactivated to align strictly with the 8 official database tables.

    Optional<OtpVerification> findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, String purpose);

    Optional<OtpVerification> findByEmailAndOtpCodeAndPurposeAndUsedFalseAndExpiryDateAfter(
            String email, String otpCode, String purpose, LocalDateTime now);
}
