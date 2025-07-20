package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception thrown when a business operation results in no content.
 */
@Getter
@AllArgsConstructor
public class BusinessNoContentRequestException extends RuntimeException {
    /**
     * Message key for no content.
     */
    public static final String NO_CONTENT = "exception.noContent";
    /**
     * The exception message.
     */
    private final String message;
    /**
     * Arguments for message formatting.
     */
    private final transient Object[] args;
}
