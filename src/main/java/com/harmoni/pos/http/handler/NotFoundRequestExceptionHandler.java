package com.harmoni.pos.http.handler;

import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * Exception handler for business operations where a resource is not found.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class NotFoundRequestExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public NotFoundRequestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles {@link BusinessNotFoundRequestException} and returns
     * a localized HTTP 404 response.
     *
     * @param e      the exception containing the message code and arguments
     * @param locale the request locale for message translation
     * @return a 404 NOT_FOUND response with the error message
     */
    @ExceptionHandler(BusinessNotFoundRequestException.class)
    public ResponseEntity<RestAPIResponse> notFoundExceptionHandler(
            BusinessNotFoundRequestException e, Locale locale) {

        String messageName = e.getMessage();
        Object[] args = e.getArgs();

        String message = messageSource.getMessage(messageName, args, locale);

        log.warn("NotFoundRequest: {}", message);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .error(message)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.NOT_FOUND);
    }
}
