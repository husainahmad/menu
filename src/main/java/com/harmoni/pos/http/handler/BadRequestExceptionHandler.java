package com.harmoni.pos.http.handler;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * Exception handler for bad request exceptions.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class BadRequestExceptionHandler {
    private final MessageSource messageSource;

    /**
     * Handles BusinessBadRequestException and returns a formatted response.
     *
     * @param e      the exception
     * @param locale the locale for message translation
     * @return ResponseEntity with error details
     */
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

        logAsWarning(message);

        return new ResponseEntity<>(restAPIResponse, HttpStatus.BAD_REQUEST);
    }

    private static void logAsWarning(String message) {
        log.warn("BadRequest: {}", message);
    }

    /**
     * Handles missing required request parameters.
     *
     * @param e the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestAPIResponse> missingRequiredParam(MissingServletRequestParameterException e) {
        String messageName = e.getMessage();

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .error(messageName)
                .data(null)
                .build();

        logAsWarning(messageName);

        return new ResponseEntity<>(restAPIResponse, HttpStatus.BAD_REQUEST);
    }
}
