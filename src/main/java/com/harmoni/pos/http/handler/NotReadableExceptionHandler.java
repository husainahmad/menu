package com.harmoni.pos.http.handler;

import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * Exception handler for unreadable HTTP messages.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class NotReadableExceptionHandler {

    /**
     * Handles {@link HttpMessageNotReadableException} and returns a bad request response.
     *
     * @param e the exception thrown when the HTTP message is not readable
     * @param locale the locale for message translation
     * @return ResponseEntity with error details and HTTP status 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestAPIResponse>
    notReadableExceptionHandler(HttpMessageNotReadableException e, Locale locale) {

        log.error("NotReadable:", e);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .error(HttpStatus.BAD_REQUEST)
                .data(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.BAD_REQUEST);
    }

}
