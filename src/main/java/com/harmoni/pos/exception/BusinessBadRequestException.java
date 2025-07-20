package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception thrown when a bad request occurs in business logic, such as not found or duplicate tier.
 */
@Getter
@AllArgsConstructor
public class BusinessBadRequestException extends RuntimeException {
    /**
     * Message key for not found tier.
     */
    public static final String NOT_FOUND_TIER = "exception.tier.id.badRequest.notFound";
    /**
     * Message key for duplicate tier.
     */
    public static final String DUPLICATION_TIER = "exception.tier.badRequest.duplicate";
    /**
     * The exception message.
     */
    private final String message;
    /**
     * Arguments for message formatting.
     */
    private final transient Object[] args;
}
