package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found in business logic.
 */
@Getter
@AllArgsConstructor
public class BusinessNotFoundRequestException extends RuntimeException {
    /**
     * Message key for not found tier.
     */
    public static final String NOT_FOUND_TIER = "exception.tier.id.badRequest.notFound";
    /**
     * The exception message.
     */
    private final String message;
    /**
     * Arguments for message formatting.
     */
    private final transient Object[] args;
}
