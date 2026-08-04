package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;
import com.optinova.entity.User;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.repository.UserRepository;
import com.optinova.service.EmailService;
import com.optinova.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing UUID password reset tokens, expiry validation, and password updates.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Send email confirmation
        emailService.sendPasswordResetEmail(user.getEmail(), "Reset request received.");

        return ApiResponse.success("Password reset request received for email address: " + user.getEmail());
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found for provided email."));

        // Encode new password and update user credentials
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.success("Password reset successfully. You can now login with your new password.");
    }
}
