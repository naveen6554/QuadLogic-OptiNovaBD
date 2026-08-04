package com.optinova.controller;

import com.optinova.dto.LogoutResponseDto;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.security.CustomUserDetails;
import com.optinova.service.LogoutService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {

    @Mock
    private LogoutService logoutService;

    @InjectMocks
    private LogoutController logoutController;

    private CustomUserDetails customUserDetails;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(101)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.CUSTOMER)
                .build();

        customUserDetails = new CustomUserDetails(testUser);
    }

    @Test
    @DisplayName("Should successfully execute logout endpoint and return 200 OK with cleared cookie")
    void logout_WhenAuthenticatedUser_ReturnsHttp200AndClearsCookie() {
        LogoutResponseDto mockResponse = LogoutResponseDto.builder()
                .message("Logout successful")
                .build();

        when(logoutService.logout(any(User.class))).thenReturn(mockResponse);

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        ResponseEntity<LogoutResponseDto> responseEntity = logoutController.logout(customUserDetails, httpServletResponse);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Logout successful", responseEntity.getBody().getMessage());

        // Verify Cookie clearing properties
        Cookie responseCookie = httpServletResponse.getCookie("jwt");
        assertNotNull(responseCookie);
        assertEquals("/", responseCookie.getPath());
        assertEquals(0, responseCookie.getMaxAge());
        assertTrue(responseCookie.isHttpOnly());

        verify(logoutService, times(1)).logout(testUser);
    }

    @Test
    @DisplayName("Should handle anonymous user cleanly and return 200 OK")
    void logout_WhenUserDetailsIsNull_ReturnsHttp200() {
        LogoutResponseDto mockResponse = LogoutResponseDto.builder()
                .message("Logout successful")
                .build();

        when(logoutService.logout(null)).thenReturn(mockResponse);

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        ResponseEntity<LogoutResponseDto> responseEntity = logoutController.logout(null, httpServletResponse);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logout successful", responseEntity.getBody().getMessage());

        verify(logoutService, times(1)).logout(null);
    }
}
