package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;
import com.optinova.entity.User;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

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
                .userId(1)
                .username("janedoe")
                .email("jane.doe@example.com")
                .password("oldPassword")
                .build();

        forgotPasswordRequest = ForgotPasswordRequest.builder()
                .email("jane.doe@example.com")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .email("jane.doe@example.com")
                .newPassword("newSecurePassword123")
                .build();
    }

    @Test
    @DisplayName("Should Successfully Process Forgot Password")
    void testForgotPasswordSuccess() {
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));

        ApiResponse<String> response = passwordResetService.forgotPassword(forgotPasswordRequest);

        assertTrue(response.isSuccess());
        verify(emailService, times(1)).sendPasswordResetEmail(eq("jane.doe@example.com"), anyString());
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when Email Not Found")
    void testForgotPasswordUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        forgotPasswordRequest.setEmail("unknown@example.com");

        assertThrows(ResourceNotFoundException.class, () -> passwordResetService.forgotPassword(forgotPasswordRequest));
    }

    @Test
    @DisplayName("Should Successfully Reset Password for Valid Email")
    void testResetPasswordSuccess() {
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newSecurePassword123")).thenReturn("encodedNewPassword");

        ApiResponse<String> response = passwordResetService.resetPassword(resetPasswordRequest);

        assertTrue(response.isSuccess());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should Throw BadRequestException when User Email Not Found for Reset")
    void testResetPasswordUserNotFound() {
        resetPasswordRequest.setEmail("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(resetPasswordRequest));
    }
}
