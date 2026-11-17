package com.harmoni.pos.http.handler;

import com.harmoni.pos.exception.ScopeAccessDeniedException;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps a scope violation to HTTP 403 so a caller cannot read or mutate data
 * belonging to another brand, chain or store.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class ScopeAccessDeniedExceptionHandler {

    /**
     * Handles {@link ScopeAccessDeniedException}.
     *
     * @param exception the scope violation
     * @return a 403 FORBIDDEN response
     */
    @ExceptionHandler(ScopeAccessDeniedException.class)
    public ResponseEntity<RestAPIResponse> scopeAccessDenied(ScopeAccessDeniedException exception) {
        log.warn("Scope access denied: {}", exception.getMessage());
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .error(exception.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
