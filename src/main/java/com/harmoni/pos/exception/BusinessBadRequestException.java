package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessBadRequestException extends RuntimeException {
    public static final String NOT_FOUND_TIER = "exception.tier.id.badRequest.notFound";
    public static final String DUPLICATION_TIER = "exception.tier.badRequest.duplicate";
    private final String message;
    private final transient Object[] args;
}
