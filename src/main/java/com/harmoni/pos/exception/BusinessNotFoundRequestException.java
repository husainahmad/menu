package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessNotFoundRequestException extends RuntimeException {
    private String message;
    private Object[] args;
}
