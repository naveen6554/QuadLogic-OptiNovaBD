package com.optinova.dto;

import com.optinova.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user profile, role, and credentials by Admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO request payload for updating user details and roles")
public class AdminUpdateUserRequest {

    @NotBlank(message = "Username cannot be blank")
    @Schema(description = "Updated unique username", example = "john_doe")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Schema(description = "Updated user email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Optional new password. Leave blank if unchanged.", example = "NewSecurePass123!")
    private String password;

    @NotNull(message = "User role is required")
    @Schema(description = "Assigned user security role (ADMIN or CUSTOMER)", example = "CUSTOMER")
    private Role role;
}
