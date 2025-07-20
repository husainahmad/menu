package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception thrown when an unauthorized request is made in business logic.
 */
@Getter
@AllArgsConstructor
public class BusinessUnAuthorizedRequestException extends RuntimeException {
    /**
     * The exception message.
     */
    private final String message;
    /**
     * Arguments for message formatting.
     */
    private final transient Object[] args;
}
