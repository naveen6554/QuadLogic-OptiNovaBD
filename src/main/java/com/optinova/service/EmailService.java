package com.optinova.service;

/**
 * Service interface for sending emails (OTP verification, password reset, order notifications).
 */
public interface EmailService {

    void sendOtpEmail(String toEmail, String otpCode, String purpose);

    void sendPasswordResetEmail(String toEmail, String resetToken);
}
