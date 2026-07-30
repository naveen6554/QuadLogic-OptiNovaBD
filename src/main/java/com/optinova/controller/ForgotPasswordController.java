package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;
import com.optinova.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing Password Recovery endpoints (Forgot Password, Reset Password).
 */
@RestController
@RequestMapping(AppConstants.AUTH_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Forgot Password Module", description = "REST APIs for Password Recovery and Token Validation")
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Generates a 15-minute expiring UUID password reset token and emails it to the user.")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ApiResponse<String> response = passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Validates the UUID reset token and updates the user password.")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse<String> response = passwordResetService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
