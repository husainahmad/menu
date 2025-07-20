package com.harmoni.pos.business.service.rest;

import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Serializable;
/**
 * Service class for making reactive HTTP REST API calls using {@link WebClient}.
 * <p>
 * Supports GET, POST, PUT, and DELETE methods with token-based authorization.
 * Automatically handles common HTTP status codes and logs errors.
 * </p>
 *
 * <p><strong>Supported Status Handlers:</strong></p>
 * <ul>
 *     <li>400 Bad Request</li>
 *     <li>401 Unauthorized</li>
 *     <li>204 No Content</li>
 *     <li>500 Internal Server Error</li>
 * </ul>
 *
 * @author husainahmad
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RestClientService implements Serializable {

    private static final String LOG_BAD_REQUEST = "BAD_REQUEST Server Response {}";
    private static final String LOG_NO_CONTENT = "LOG_NO_CONTENT Server Response {}";
    private static final String LOG_UN_AUTHORIZED = "LOG_UN_AUTHORIZED Server Response {}";
    private static final String LOG_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR Server Response {}";
    private static final String BEARER = "Bearer ";
    private static final WebClient webClient = WebClient.builder().build();

    /**
     * Sends a POST request with the given request body and authentication token.
     *
     * @param token      Bearer token
     * @param url        Request URL
     * @param publisher  Request body as a {@link Publisher}
     * @param className  Class type of the body
     * @return {@link Mono} emitting {@link RestAPIResponse}
     */
    public Mono<RestAPIResponse> post(String token, String url, Publisher<?> publisher, Class<?> className) {
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER.concat(token))
                .body(publisher, className)
                .retrieve()
                .onStatus(HttpStatus.NO_CONTENT::equals, this::handleNoContent)
                .onStatus(HttpStatus.BAD_REQUEST::equals, this::handleBadRequest)
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, this::handleInternalServerError)
                .onStatus(HttpStatus.UNAUTHORIZED::equals, this::handleUnAuthorized)
                .bodyToMono(RestAPIResponse.class);
    }

    /**
     * Sends a GET request with Bearer token authorization.
     *
     * @param token Bearer token
     * @param url   Request URL
     * @return {@link Mono} emitting {@link RestAPIResponse}
     */
    public Mono<RestAPIResponse> get(String token, String url) {
        WebClient.ResponseSpec retrieve = webClient.get()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER.concat(token))
                .retrieve();

        retrieve.onStatus(HttpStatus.NO_CONTENT::equals, this::handleNoContent);
        retrieve.onStatus(HttpStatus.BAD_REQUEST::equals, this::handleBadRequest);
        retrieve.onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, this::handleInternalServerError);
        retrieve.onStatus(HttpStatus.UNAUTHORIZED::equals, this::handleUnAuthorized);

        return retrieve.bodyToMono(RestAPIResponse.class);
    }

    /**
     * Sends a PUT request with the given body and token.
     *
     * @param token      Bearer token
     * @param url        Request URL
     * @param publisher  Request body
     * @param className  Class type of the body
     * @return {@link Mono} emitting {@link RestAPIResponse}
     */
    public Mono<RestAPIResponse> put(String token, String url, Publisher<?> publisher, Class<?> className) {
        return webClient.put()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER.concat(token))
                .body(publisher, className)
                .retrieve()
                .onStatus(HttpStatus.NO_CONTENT::equals, this::handleNoContent)
                .onStatus(HttpStatus.BAD_REQUEST::equals, this::handleBadRequest)
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, this::handleInternalServerError)
                .onStatus(HttpStatus.UNAUTHORIZED::equals, this::handleUnAuthorized)
                .bodyToMono(RestAPIResponse.class);
    }

    /**
     * Sends a DELETE request with Bearer token.
     *
     * @param token Bearer token
     * @param url   Request URL
     * @return {@link Mono} emitting {@link RestAPIResponse}
     */
    public Mono<RestAPIResponse> delete(String token, String url) {
        return webClient.delete()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER.concat(token))
                .retrieve()
                .onStatus(HttpStatus.NO_CONTENT::equals, this::handleNoContent)
                .onStatus(HttpStatus.BAD_REQUEST::equals, this::handleBadRequest)
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, this::handleInternalServerError)
                .onStatus(HttpStatus.UNAUTHORIZED::equals, this::handleUnAuthorized)
                .bodyToMono(RestAPIResponse.class);
    }

    /**
     * Logs error messages with the given log format and status.
     *
     * @param logTemplate Log template
     * @param response    {@link RestAPIResponse} containing the status
     */
    private static void logError(String logTemplate, RestAPIResponse response) {
        log.error(logTemplate, response.getHttpStatus());
    }

    /**
     * Handles HTTP 400 Bad Request response.
     */
    private Mono<? extends Throwable> handleBadRequest(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(RestAPIResponse.class)
                .handle((res, sink) -> logError(LOG_BAD_REQUEST, res));
    }

    /**
     * Handles HTTP 204 No Content response.
     */
    private Mono<? extends Throwable> handleNoContent(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(RestAPIResponse.class)
                .handle((res, sink) -> logError(LOG_NO_CONTENT, res));
    }

    /**
     * Handles HTTP 401 Unauthorized response.
     */
    private Mono<? extends Throwable> handleUnAuthorized(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(RestAPIResponse.class)
                .handle((res, sink) -> logError(LOG_UN_AUTHORIZED, res));
    }

    /**
     * Handles HTTP 500 Internal Server Error response.
     */
    private Mono<? extends Throwable> handleInternalServerError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(RestAPIResponse.class)
                .handle((res, sink) -> logError(LOG_INTERNAL_SERVER_ERROR, res));
    }
}
