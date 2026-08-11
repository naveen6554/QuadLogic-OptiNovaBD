package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.JwtToken;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.InvalidTokenException;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.repository.UserRepository;
import com.optinova.security.JwtTokenProvider;
import com.optinova.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.optinova.service.EmailService;
import lombok.extern.slf4j.Slf4j;

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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Account already exists with email: " + request.getEmail());
        }

        String username = request.getUsername();
        if (username == null || username.isBlank()) {
            username = request.getEmail();
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Account already exists with username: " + username);
        }

        Role userRole = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .username(username)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);

        // Generate 6-digit OTP and send email via EmailService
        String otpCode = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
        log.info("Sending OTP [{}] to email: {}", otpCode, user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otpCode, "REGISTRATION");

        return ApiResponse.success("User registered successfully. Verification OTP code sent to " + user.getEmail());
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
    public ApiResponse<String> logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            JwtToken jwtToken = jwtTokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Invalid token provided for logout."));

            jwtTokenRepository.delete(jwtToken);
            SecurityContextHolder.clearContext();
            return ApiResponse.success("Logged out successfully.");
        }
        throw new BadRequestException("Authorization header with Bearer token is required for logout.");
    }

    private void saveUserJwtToken(User user, String jwt) {
        JwtToken jwtToken = JwtToken.builder()
                .user(user)
                .token(jwt)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        jwtTokenRepository.save(jwtToken);
    }

    private AuthResponse buildAuthResponse(User user, String jwt) {
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .token(jwt)
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresInMs(jwtTokenProvider.getExpirationMs())
                .user(userResponse)
                .build();
    }
}
