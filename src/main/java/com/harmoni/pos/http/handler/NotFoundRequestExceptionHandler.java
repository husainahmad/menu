package com.harmoni.pos.http.handler;

import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.response.RestAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
public class NotFoundRequestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(NotFoundRequestExceptionHandler.class);
    private final MessageSource messageSource;

    @Autowired
    public NotFoundRequestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessNotFoundRequestException.class)
    public ResponseEntity<RestAPIResponse>
            badRequestExceptionHandler(BusinessNotFoundRequestException e, Locale locale) {

        String messageName = e.getMessage();
        Object[] args = e.getArgs();

        String message = messageSource.getMessage(messageName, args, locale);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .error(message)
                .build();

        log.warn("NotFoundRequest: {}", message);

        return new ResponseEntity<>(restAPIResponse, HttpStatus.BAD_REQUEST);
    }
}
