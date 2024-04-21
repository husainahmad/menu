package com.harmoni.pos.http.handler;

import com.harmoni.pos.http.response.RestAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ValidationExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestAPIResponse>
    handleValidationException(MethodArgumentNotValidException e, Locale locale) {

        List<String> errors = new ArrayList<String>();

        for (FieldError fieldError: e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + ">" + fieldError.getDefaultMessage());
        }

        for (ObjectError objectError: e.getBindingResult().getGlobalErrors()) {
            errors.add(objectError.getObjectName() + ">" + objectError.getDefaultMessage());
        }

        RestAPIResponse genericResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .error(errors)
                .build();

        log.warn("Validation: {}", e.getMessage());

        return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }
}
