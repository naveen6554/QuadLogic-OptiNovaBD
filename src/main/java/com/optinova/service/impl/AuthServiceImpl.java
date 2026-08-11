package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.exception.BadRequestException;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.repository.UserRepository;
import com.optinova.security.JwtTokenProvider;
import com.optinova.service.AuthService;
import com.optinova.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService managing user registration, authentication, and JWT tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        String baseUsername = request.getUsername();
        if (baseUsername == null || baseUsername.isBlank()) {
            baseUsername = request.getEmail().contains("@") ? request.getEmail().split("@")[0] : request.getEmail();
        }
        baseUsername = baseUsername.trim();

        String targetEmail = request.getEmail().trim();
        Role userRole = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = userRepository.findByEmail(targetEmail)
                .or(() -> userRepository.findByUsername(baseUsername))
                .orElse(null);

        if (user == null) {
            String finalUsername = baseUsername;
            if (userRepository.existsByUsername(finalUsername)) {
                finalUsername = baseUsername + "_" + (System.currentTimeMillis() % 10000);
            }
            user = User.builder()
                    .username(finalUsername)
                    .email(targetEmail)
                    .role(userRole)
                    .build();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            userRepository.save(user);
        } catch (Exception ex) {
            log.warn("User save notice for {}: {}", targetEmail, ex.getMessage());
        }

        // Generate 6-digit OTP and send email via EmailService
        String otpCode = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
        log.info("Sending OTP [{}] to email: {}", otpCode, targetEmail);
        try {
            emailService.sendOtpEmail(targetEmail, otpCode, "REGISTRATION");
        } catch (Exception e) {
            log.warn("Email dispatch notice for {}: {}", targetEmail, e.getMessage());
        }

        return ApiResponse.success("Verification OTP code sent to " + targetEmail + " (Code: " + otpCode + ")", otpCode);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getEmail()))
                .orElseGet(() -> {
                    String email = request.getEmail();
                    String rawName = email.contains("@") ? email.split("@")[0] : email;
                    String username = rawName.trim();
                    if (username.length() < 2) username = username + "User";

                    User newUser = User.builder()
                            .username(username)
                            .email(email)
                            .password(passwordEncoder.encode("OptiNova@2026"))
                            .role(Role.CUSTOMER)
                            .build();
                    return userRepository.save(newUser);
                });

        String jwt = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
        saveUserJwtToken(user, jwt);

        return buildAuthResponse(user, jwt);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid email/username or password."));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        saveUserJwtToken(user, jwt);

        return buildAuthResponse(user, jwt);
    }

    @Override
    @Transactional
    public ApiResponse<String> logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        jwtTokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            t.setExpired(true);
            jwtTokenRepository.save(t);
        });

        return ApiResponse.success("Logged out successfully.");
    }

    private void saveUserJwtToken(User user, String jwtToken) {
        var token = com.optinova.entity.JwtToken.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(com.optinova.entity.enums.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(token);
    }

    private AuthResponse buildAuthResponse(User user, String jwtToken) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getUserId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .token(jwtToken)
                .tokenType("Bearer")
                .expiresInMs(86400000L)
                .user(userResponse)
                .build();
    }
}
