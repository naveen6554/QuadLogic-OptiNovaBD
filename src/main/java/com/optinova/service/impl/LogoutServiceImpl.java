package com.optinova.service.impl;

import com.optinova.dto.LogoutResponseDto;
import com.optinova.entity.User;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.service.LogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enterprise Service Implementation handling user logout and JWT session revocation.
 * Guarantees idempotent execution when deleting active tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final JwtTokenRepository jwtTokenRepository;

    /**
     * Identifies the user and triggers session revocation.
     *
     * @param user Currently authenticated user entity
     * @return LogoutResponseDto with status message
     */
    @Override
    @Transactional
    public LogoutResponseDto logout(User user) {
        if (user == null || user.getUserId() == null) {
            log.warn("Logout invoked with null User or unassigned userId. Returning default response.");
            return LogoutResponseDto.builder()
                    .message("Logout successful")
                    .build();
        }
        return logoutByUserId(user.getUserId());
    }

    /**
     * Retrieves and deletes stored JWT tokens for the specified userId.
     * Operation is fully idempotent and succeeds cleanly even if no token exists.
     *
     * @param userId Primary key of the authenticated user
     * @return LogoutResponseDto with status message
     */
    @Override
    @Transactional
    public LogoutResponseDto logoutByUserId(Integer userId) {
        log.info("Processing logout for user ID: {}", userId);

        if (userId == null) {
            log.warn("Null userId provided to logoutByUserId.");
            return LogoutResponseDto.builder()
                    .message("Logout successful")
                    .build();
        }

        // Retrieve existing token for user if present
        jwtTokenRepository.findByUserUserId(userId).ifPresentOrElse(
                jwtToken -> {
                    jwtTokenRepository.delete(jwtToken);
                    log.info("Successfully revoked JWT token for user ID: {}", userId);
                },
                () -> log.info("No active JWT token found in database for user ID: {}. Proceeding idempotently.", userId)
        );

        return LogoutResponseDto.builder()
                .message("Logout successful")
                .build();
    }
}
