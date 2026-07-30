package com.optinova.service;

import com.optinova.dto.*;
import com.optinova.entity.OtpVerification;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.repository.OtpVerificationRepository;
import com.optinova.repository.UserRepository;
import com.optinova.security.JwtTokenProvider;
import com.optinova.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private VerifyOtpRequest verifyOtpRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("Password123")
                .phone("9876543210")
                .build();

        verifyOtpRequest = VerifyOtpRequest.builder()
                .email("john.doe@example.com")
                .otpCode("123456")
                .build();
    }

    @Test
    @DisplayName("Should Successfully Register New User and Send OTP Email")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        ApiResponse<String> response = authService.register(registerRequest);

        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
        verify(otpVerificationRepository, times(1)).save(any(OtpVerification.class));
        verify(emailService, times(1)).sendOtpEmail(eq("john.doe@example.com"), anyString(), eq("REGISTRATION"));
    }

    @Test
    @DisplayName("Should Throw DuplicateResourceException when Email Exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should Successfully Verify OTP and Activate Account")
    void testVerifyOtpSuccess() {
        OtpVerification otp = OtpVerification.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .purpose("REGISTRATION")
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        User user = User.builder()
                .id(10L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.ROLE_USER)
                .isVerified(false)
                .build();

        when(otpVerificationRepository.findByEmailAndOtpCodeAndPurposeAndUsedFalseAndExpiryDateAfter(
                eq("john.doe@example.com"), eq("123456"), eq("REGISTRATION"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otp));

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateTokenFromEmail("john.doe@example.com")).thenReturn("mock.jwt.token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        AuthResponse authResponse = authService.verifyOtp(verifyOtpRequest);

        assertNotNull(authResponse);
        assertEquals("mock.jwt.token", authResponse.getAccessToken());
        assertTrue(user.isVerified());
        assertTrue(otp.isUsed());
        verify(jwtTokenRepository, times(1)).save(any());
    }
}
