package com.optinova.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User Login Request.
 * Accepts either Email address or Username identifier.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email or Username is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
