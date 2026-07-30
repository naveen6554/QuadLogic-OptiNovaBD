package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.*;
import com.optinova.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user credentials and returns JWT Access Token.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Revokes current JWT token and invalidates active session.")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader(AppConstants.HEADER_STRING) String token) {
        ApiResponse<String> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }
}
