package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessBadRequestException extends RuntimeException {
    private final String message;
    private final transient Object[] args;
}
