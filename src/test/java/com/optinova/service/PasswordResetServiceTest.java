package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;
import com.optinova.entity.PasswordResetToken;
import com.optinova.entity.User;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.repository.PasswordResetTokenRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .password("oldPassword")
                .isVerified(true)
                .build();

        forgotPasswordRequest = ForgotPasswordRequest.builder()
                .email("jane.doe@example.com")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .token(UUID.randomUUID().toString())
                .newPassword("newSecurePassword123")
                .build();
    }

    @Test
    @DisplayName("Should Successfully Generate UUID Token and Email User on Forgot Password")
    void testForgotPasswordSuccess() {
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));

        ApiResponse<String> response = passwordResetService.forgotPassword(forgotPasswordRequest);

        assertTrue(response.isSuccess());
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendPasswordResetEmail(eq("jane.doe@example.com"), anyString());
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when Email Not Found")
    void testForgotPasswordUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        forgotPasswordRequest.setEmail("unknown@example.com");

        assertThrows(ResourceNotFoundException.class, () -> passwordResetService.forgotPassword(forgotPasswordRequest));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should Successfully Reset Password with Valid Token")
    void testResetPasswordSuccess() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(10L)
                .token(resetPasswordRequest.getToken())
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        when(tokenRepository.findByTokenAndUsedFalseAndExpiryDateAfter(
                eq(resetPasswordRequest.getToken()), any(LocalDateTime.class)))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("newSecurePassword123")).thenReturn("encodedNewPassword");

        ApiResponse<String> response = passwordResetService.resetPassword(resetPasswordRequest);

        assertTrue(response.isSuccess());
        assertEquals("encodedNewPassword", user.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    @DisplayName("Should Throw BadRequestException when Reset Token Expired or Invalid")
    void testResetPasswordInvalidToken() {
        when(tokenRepository.findByTokenAndUsedFalseAndExpiryDateAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(resetPasswordRequest));
    }
}
