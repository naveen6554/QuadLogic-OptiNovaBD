package com.optinova.dto;

import com.optinova.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing user account profile details returned in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public Integer getId() {
        return userId;
    }
}
