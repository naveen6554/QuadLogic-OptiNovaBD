package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ForgotPasswordRequest;
import com.optinova.dto.ResetPasswordRequest;

/**
 * Service interface for Forgot Password and Reset Password workflows.
 */
public interface PasswordResetService {

    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<String> resetPassword(ResetPasswordRequest request);
}
