package com.optinova.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a JWT token or OTP code has passed its expiration lifespan.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
