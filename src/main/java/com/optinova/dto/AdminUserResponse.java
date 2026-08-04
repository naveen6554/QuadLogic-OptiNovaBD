package com.optinova.dto;

import com.optinova.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returning user details for Admin management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO response containing user profile details for Admin view")
public class AdminUserResponse {

    @Schema(description = "User primary key ID", example = "1")
    private Integer userId;

    @Schema(description = "User unique username", example = "john_doe")
    private String username;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Assigned security role", example = "CUSTOMER")
    private Role role;

    @Schema(description = "Account registration timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last account update timestamp")
    private LocalDateTime updatedAt;
}
