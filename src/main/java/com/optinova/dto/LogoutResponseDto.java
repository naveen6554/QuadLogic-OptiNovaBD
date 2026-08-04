package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returning the result of a logout operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for user logout operation")
public class LogoutResponseDto {

    @Schema(description = "Confirmation message", example = "Logout successful")
    private String message;
}
