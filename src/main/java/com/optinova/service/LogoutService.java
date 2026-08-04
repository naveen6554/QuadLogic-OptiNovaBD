package com.optinova.service;

import com.optinova.dto.LogoutResponseDto;
import com.optinova.entity.User;

/**
 * Service interface defining functional contract for secure user logout operations.
 */
public interface LogoutService {

    /**
     * Executes logout for the given authenticated User entity.
     * Revokes active JWT token from the database and returns a response DTO.
     *
     * @param user Currently authenticated user entity
     * @return LogoutResponseDto containing confirmation message
     */
    LogoutResponseDto logout(User user);

    /**
     * Executes logout by user ID.
     * Revokes active JWT token associated with the user ID from the database.
     *
     * @param userId Primary key of the user
     * @return LogoutResponseDto containing confirmation message
     */
    LogoutResponseDto logoutByUserId(Integer userId);
}
