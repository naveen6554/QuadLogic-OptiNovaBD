package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.JwtToken;
import com.optinova.entity.OtpVerification;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.entity.enums.TokenType;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.ExpiredTokenException;
import com.optinova.exception.InvalidTokenException;
import com.optinova.repository.JwtTokenRepository;
import com.optinova.repository.OtpVerificationRepository;
import com.optinova.repository.UserRepository;
import com.optinova.security.JwtTokenProvider;
import com.optinova.service.AuthService;
import com.optinova.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of AuthService managing user registration, OTP validation, authentication, and JWT token revocation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    private static final String OTP_PURPOSE_REGISTRATION = "REGISTRATION";
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        // Build & save new user (isVerified = false initially)
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ROLE_USER)
                .isVerified(false)
                .build();

        userRepository.save(user);

        // Generate 6-digit random OTP
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));

        OtpVerification otp = OtpVerification.builder()
                .email(request.getEmail())
                .otpCode(otpCode)
                .purpose(OTP_PURPOSE_REGISTRATION)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        otpVerificationRepository.save(otp);

        // Send OTP email
        emailService.sendOtpEmail(request.getEmail(), otpCode, OTP_PURPOSE_REGISTRATION);

        return ApiResponse.success("User registered successfully. Please verify the OTP sent to " + request.getEmail() + " to activate your account.");
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        OtpVerification otp = otpVerificationRepository
                .findByEmailAndOtpCodeAndPurposeAndUsedFalseAndExpiryDateAfter(
                        request.getEmail(),
                        request.getOtpCode(),
                        OTP_PURPOSE_REGISTRATION,
                        LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP code."));

        // Mark OTP used
        otp.setUsed(true);
        otpVerificationRepository.save(otp);

        // Activate User
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found for email: " + request.getEmail()));

        user.setVerified(true);
        userRepository.save(user);

        // Generate JWT token
        String jwt = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
        saveUserJwtToken(user, jwt);

        return buildAuthResponse(user, jwt);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        if (!user.isVerified()) {
            throw new BadRequestException("Account is not verified. Please verify the OTP sent to your email.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        // Revoke previous active tokens and save new token
        revokeAllUserTokens(user);
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

            jwtToken.setRevoked(true);
            jwtToken.setExpired(true);
            jwtTokenRepository.save(jwtToken);
            SecurityContextHolder.clearContext();
            return ApiResponse.success("Logged out successfully.");
        }
        throw new BadRequestException("Authorization header with Bearer token is required for logout.");
    }

    private void saveUserJwtToken(User user, String jwt) {
        JwtToken jwtToken = JwtToken.builder()
                .user(user)
                .token(jwt)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(jwtToken);
    }

    private void revokeAllUserTokens(User user) {
        List<JwtToken> validUserTokens = jwtTokenRepository.findAllValidTokensByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            jwtTokenRepository.saveAll(validUserTokens);
        }
    }

    private AuthResponse buildAuthResponse(User user, String jwt) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresInMs(jwtTokenProvider.getExpirationMs())
                .user(userResponse)
                .build();
    }
}
