package com.optinova.controller;

import com.optinova.dto.LogoutResponseDto;
import com.optinova.entity.User;
import com.optinova.security.CustomUserDetails;
import com.optinova.service.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Enterprise REST Controller exposing secure user logout endpoint.
 * Accepts POST requests at /api/auth/logout, revokes JWT token, and clears auth cookie.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Logout Module", description = "REST API for User Logout and Session Revocation")
public class LogoutController {

    private final LogoutService logoutService;

    /**
     * Endpoint to perform secure user logout.
     * Identifies authenticated user from Security Context, revokes database JWT token,
     * clears authentication cookie, and returns HTTP 200.
     *
     * @param userDetails Authenticated UserDetails injected by Spring Security
     * @param response HTTP Servlet Response used for setting set-cookie headers
     * @return ResponseEntity containing LogoutResponseDto
     */
    @PostMapping("/logout")
    @Operation(
            summary = "User Logout",
            description = "Logs out the authenticated user, revokes JWT token from jwt_tokens table, and clears authentication cookie.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(schema = @Schema(implementation = LogoutResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication token missing or invalid"
            )
    })
    public ResponseEntity<LogoutResponseDto> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response) {

        User user = (userDetails != null) ? userDetails.getUser() : null;

        LogoutResponseDto logoutResponse = logoutService.logout(user);

        // 1. Clear Servlet Cookie
        Cookie servletCookie = new Cookie("jwt", null);
        servletCookie.setPath("/");
        servletCookie.setMaxAge(0);
        servletCookie.setHttpOnly(true);
        response.addCookie(servletCookie);

        // 2. Add ResponseCookie header for modern browsers
        ResponseCookie responseCookie = ResponseCookie.from("jwt", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        log.info("Logout API executed successfully for user: {}", user != null ? user.getEmail() : "unknown");

        return ResponseEntity.ok(logoutResponse);
    }
}
