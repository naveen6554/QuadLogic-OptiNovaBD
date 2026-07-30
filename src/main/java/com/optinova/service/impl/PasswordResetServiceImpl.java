package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;
import com.optinova.entity.PasswordResetToken;
import com.optinova.entity.User;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.repository.PasswordResetTokenRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.EmailService;
import com.optinova.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service implementation managing UUID password reset tokens, expiry validation, and password updates.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Generate 15-minute expiring UUID token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send email via Gmail SMTP
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return ApiResponse.success("Password reset token has been sent to your email address: " + user.getEmail());
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiryDateAfter(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token."));

        User user = resetToken.getUser();

        // Encode new password and update user credentials
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return ApiResponse.success("Password reset successfully. You can now login with your new password.");
    }
}
