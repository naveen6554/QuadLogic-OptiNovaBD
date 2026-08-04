package com.optinova.service;

import com.optinova.dto.LogoutResponseDto;
import com.optinova.entity.JwtToken;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.service.impl.LogoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @InjectMocks
    private LogoutServiceImpl logoutService;

    private User testUser;
    private JwtToken testToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(101)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.CUSTOMER)
                .build();

        testToken = JwtToken.builder()
                .tokenId(1)
                .user(testUser)
                .token("mock-jwt-token-string")
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
    }

    @Test
    @DisplayName("Should successfully delete token when existing token found for user")
    void logout_WhenTokenExists_DeletesTokenAndReturnsSuccess() {
        when(jwtTokenRepository.findByUserUserId(101)).thenReturn(Optional.of(testToken));

        LogoutResponseDto response = logoutService.logout(testUser);

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        verify(jwtTokenRepository, times(1)).findByUserUserId(101);
        verify(jwtTokenRepository, times(1)).delete(testToken);
    }

    @Test
    @DisplayName("Should return success idempotently when token does not exist")
    void logout_WhenTokenDoesNotExist_ReturnsSuccessWithoutException() {
        when(jwtTokenRepository.findByUserUserId(101)).thenReturn(Optional.empty());

        LogoutResponseDto response = logoutService.logout(testUser);

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        verify(jwtTokenRepository, times(1)).findByUserUserId(101);
        verify(jwtTokenRepository, never()).delete(any(JwtToken.class));
    }

    @Test
    @DisplayName("Should handle null user safely and return success message")
    void logout_WhenUserIsNull_ReturnsSuccessMessage() {
        LogoutResponseDto response = logoutService.logout(null);

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        verify(jwtTokenRepository, never()).findByUserUserId(anyInt());
        verify(jwtTokenRepository, never()).delete(any(JwtToken.class));
    }

    @Test
    @DisplayName("Should handle null userId safely when invoking logoutByUserId")
    void logoutByUserId_WhenUserIdIsNull_ReturnsSuccessMessage() {
        LogoutResponseDto response = logoutService.logoutByUserId(null);

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        verify(jwtTokenRepository, never()).findByUserUserId(anyInt());
    }
}
