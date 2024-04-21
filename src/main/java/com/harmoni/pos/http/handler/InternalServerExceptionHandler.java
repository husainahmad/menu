package com.harmoni.pos.http.handler;

import com.harmoni.pos.http.response.RestAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class InternalServerExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(InternalServerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestAPIResponse> globalExceptionHandler(Exception exception,
                                                                  WebRequest request) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timeStamp(System.currentTimeMillis())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();

        log.error("FightBegun: {}", exception.getMessage());
        return new ResponseEntity<RestAPIResponse>(restAPIResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
