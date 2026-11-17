package com.harmoni.pos.http.handler;

import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps a Spring Security method-level denial to HTTP 403.
 *
 * <p>Without this advice the catch-all {@link InternalServerExceptionHandler}
 * would turn an {@link AccessDeniedException} into a 500, hiding the fact that
 * the caller simply lacks the required authority.</p>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class AccessDeniedExceptionHandler {

    /**
     * Handles {@link AccessDeniedException}.
     *
     * @param exception the denied authorization
     * @return a 403 FORBIDDEN response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestAPIResponse> accessDenied(AccessDeniedException exception) {
        log.warn("Access denied: {}", exception.getMessage());
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .error(HttpStatus.FORBIDDEN)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
