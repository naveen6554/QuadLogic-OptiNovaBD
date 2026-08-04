package com.optinova.service;

import com.optinova.dto.*;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.repository.JwtTokenRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

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

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .password("Password123")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("Should Successfully Register New User")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        ApiResponse<String> response = authService.register(registerRequest);

        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should Throw DuplicateResourceException when Email Exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
