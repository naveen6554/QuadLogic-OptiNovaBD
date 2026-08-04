package com.optinova.repository;

import com.optinova.entity.PasswordResetToken;
import com.optinova.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for PasswordResetToken entity management.
 */
// @Repository
public interface PasswordResetTokenRepository {
// Deactivated to align strictly with the 8 official database tables.

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiryDateAfter(String token, LocalDateTime now);

    Optional<PasswordResetToken> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}
