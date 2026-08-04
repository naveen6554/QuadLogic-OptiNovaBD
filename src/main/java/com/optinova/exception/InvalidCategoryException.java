package com.optinova.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an operation specifies an invalid or non-existent Category.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCategoryException extends RuntimeException {

    public InvalidCategoryException(String message) {
        super(message);
    }
}
