package com.optinova.service.impl;

import com.optinova.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService using Spring JavaMailSender for Gmail SMTP delivery.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:naveenk8815@gmail.com}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("OptiNova - Your Verification Code (" + purpose + ")");
            message.setText("Welcome to OptiNova Optical Glasses E-Commerce!\n\n" +
                    "Your One-Time Password (OTP) for " + purpose + " is: " + otpCode + "\n\n" +
                    "This OTP is valid for 15 minutes. Please do not share this code with anyone.\n\n" +
                    "Best regards,\nOptiNova Security Team");

            mailSender.send(message);
            log.info("=========================================================");
            log.info("Successfully sent OTP email to {}. OTP CODE: [{}]", toEmail, otpCode);
            log.info("=========================================================");
        } catch (Exception ex) {
            log.error("=========================================================");
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
            log.error("DEVELOPMENT FALLBACK OTP CODE FOR {}: [{}]", toEmail, otpCode);
            log.error("=========================================================");
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("OptiNova - Password Reset Token");
            message.setText("You have requested a password reset for your OptiNova account.\n\n" +
                    "Your Password Reset Token is: " + resetToken + "\n\n" +
                    "This token will expire in 15 minutes.\n\n" +
                    "Best regards,\nOptiNova Security Team");

            mailSender.send(message);
            log.info("Successfully sent Password Reset email to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send Password Reset email to {}: {}", toEmail, ex.getMessage());
        }
    }
}
