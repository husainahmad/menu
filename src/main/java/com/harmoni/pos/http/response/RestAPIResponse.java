package com.harmoni.pos.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Standard response object for REST API endpoints.
 * Contains timestamp, HTTP status, data, and error details.
 */
@Getter
@Builder
public class RestAPIResponse {
    /**
     * The timestamp when the response is created.
     */
    @Builder.Default
    private long timeStamp = System.currentTimeMillis();

    /**
     * The HTTP status code of the response.
     */
    @Builder.Default
    private int httpStatus = HttpStatus.CREATED.value();

    /**
     * The data returned by the API, if any.
     */
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data = HttpStatus.CREATED;

    /**
     * The error details, if any.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;
}
