package com.optinova.service;

import com.optinova.dto.*;

/**
 * Service interface for User Registration, OTP Verification, Login, and Logout handling.
 */
public interface AuthService {

    ApiResponse<String> register(RegisterRequest registerRequest);

    AuthResponse verifyOtp(VerifyOtpRequest verifyOtpRequest);

    AuthResponse login(LoginRequest loginRequest);

    ApiResponse<String> logout(String token);
}
