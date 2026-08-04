package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object returned upon successful authentication containing JWT tokens and user metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String accessToken;
    private String tokenType;
    private long expiresInMs;
    private UserResponse user;
}
