package com.harmoni.pos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessNoContentRequestException extends RuntimeException {
    private String message;
    private Object[] args;
}
