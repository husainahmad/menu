package com.harmoni.pos.http.handler;

import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class InternalServerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestAPIResponse> globalExceptionHandler(Exception exception,
                                                                  WebRequest request) {
        log.error("FightBegun: {}", exception.getMessage());

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timeStamp(System.currentTimeMillis())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
