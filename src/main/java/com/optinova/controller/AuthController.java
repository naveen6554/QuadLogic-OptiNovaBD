package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.*;
import com.optinova.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing user authentication APIs (Register, OTP Verification, Login, Logout).
 */
@RestController
@RequestMapping(AppConstants.AUTH_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Authentication Module", description = "REST APIs for User Registration, OTP Verification, Login, and Logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registers a new user and sends a 6-digit OTP code to the provided email.")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<String> response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verifies the 6-digit email OTP and activates the user account, returning a JWT token.")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        AuthResponse response = authService.verifyOtp(verifyOtpRequest);
        return buildAuthResponseEntity(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user credentials and returns JWT Access Token.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return buildAuthResponseEntity(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Revokes current JWT token and invalidates active session.")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader(AppConstants.HEADER_STRING) String token) {
        ApiResponse<String> response = authService.logout(token);
        
        ResponseCookie nullAuthToken = ResponseCookie.from("authToken", "null")
                .httpOnly(false).path("/").maxAge(86400).sameSite("Lax").build();
        ResponseCookie nullToken = ResponseCookie.from("token", "null")
                .httpOnly(false).path("/").maxAge(86400).sameSite("Lax").build();
        ResponseCookie nullOptiToken = ResponseCookie.from("optinova_token", "null")
                .httpOnly(false).path("/").maxAge(86400).sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, nullAuthToken.toString())
                .header(HttpHeaders.SET_COOKIE, nullToken.toString())
                .header(HttpHeaders.SET_COOKIE, nullOptiToken.toString())
                .body(response);
    }

    private ResponseEntity<AuthResponse> buildAuthResponseEntity(AuthResponse response) {
        String jwtToken = response.getToken() != null ? response.getToken() : response.getAccessToken();

        ResponseCookie authTokenCookie = ResponseCookie.from("authToken", jwtToken)
                .httpOnly(false)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();

        ResponseCookie tokenCookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(false)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();

        ResponseCookie optiCookie = ResponseCookie.from("optinova_token", jwtToken)
                .httpOnly(false)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, optiCookie.toString())
                .body(response);
    }
}
