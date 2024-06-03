package com.harmoni.pos.http.handler;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.response.RestAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BadRequestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(BadRequestExceptionHandler.class);
    private final MessageSource messageSource;

    @Autowired
    public BadRequestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessBadRequestException.class)
    public ResponseEntity<RestAPIResponse>
            badRequestExceptionHandler(BusinessBadRequestException e, Locale locale) {

        String messageName = e.getMessage();
        Object[] args = e.getArgs();

        String message = messageSource.getMessage(messageName, args, locale);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .error(message)
                .data(null)
                .build();

        log.warn("BadRequest: {}", message);

        return new ResponseEntity<>(restAPIResponse, HttpStatus.BAD_REQUEST);
    }
}
