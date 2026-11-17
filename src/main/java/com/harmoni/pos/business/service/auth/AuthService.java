package com.harmoni.pos.business.service.auth;

import com.harmoni.pos.business.service.rest.RestClientService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.config.AuthUrlProperties;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service class for handling authentication-related operations such as
 * user registration, deletion, and updates via external REST APIs.
 * <p>
 * This service acts as a bridge between internal components and external
 * authentication endpoints.
 * </p>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements Serializable {

    private final RestClientService restClientService;
    private final AuthUrlProperties authUrlProperties;

    /**
     * Resolves the auth user ids whose assignments match the role/scope filter.
     *
     * @param token     JWT token for authentication
     * @param roleId    optional role id
     * @param scopeType optional scope type
     * @return the matching auth user ids, never {@code null}
     */
    public List<Long> findUserIds(String token, Integer roleId, String scopeType) {
        String url = authUrlProperties.getUserIds()
                + "?roleId=" + (roleId == null ? "" : roleId)
                + "&scopeType=" + (scopeType == null ? "" : scopeType);
        RestAPIResponse response = restClientService.get(token, url).block();
        if (response == null || !(response.getData() instanceof List<?> rows)) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (Object row : rows) {
            if (row instanceof Number number) {
                ids.add(number.longValue());
            }
        }
        return ids;
    }

    /**
     * Sends a POST request to the external authentication service to register a new user.
     *
     * @param token    JWT token for authentication
     * @param userDto  the user data to register
     * @return the created authentication user id, or {@code 0} when registration failed
     */
    public Integer create(String token, UserDto userDto) {
        AtomicReference<Integer> authUserId = new AtomicReference<>(0);
        restClientService.post(token, authUrlProperties.getRegister(), Mono.just(userDto), UserDto.class).map(restAPIResponse -> {
            if (restAPIResponse != null && restAPIResponse.getData() != null) {
                authUserId.set(Integer.valueOf(restAPIResponse.getData().toString()));
            }
            return authUserId;
        }).block();
        return authUserId.get();
    }

    /**
     * Sends a DELETE request to the external authentication service to remove a user by username.
     *
     * @param token    JWT token for authentication
     * @param username the username of the user to delete
     * @return the HTTP status code returned by the external service
     */
    public int delete(String token, String username) {
        AtomicInteger statusCode = new AtomicInteger();
        restClientService.delete(token, authUrlProperties.getDelete().concat("/").concat(username)).map(restAPIResponse -> {
            if (restAPIResponse != null && restAPIResponse.getData() != null) {
                statusCode.set(restAPIResponse.getHttpStatus());
            }
            return statusCode;
        }).block();
        return statusCode.get();
    }

    /**
     * Sends a PUT request to the external authentication service to update a user's information.
     *
     * @param token       JWT token for authentication
     * @param userEditDto the user data to update
     * @return the HTTP status code returned by the external service
     */
    public int update(String token, UserEditDto userEditDto) {
        AtomicInteger statusCode = new AtomicInteger();
        restClientService.put(token, authUrlProperties.getUpdate().concat("/").concat(userEditDto.getUsername()), Mono.just(userEditDto), UserEditDto.class)
                .map(restAPIResponse -> {
                    if (restAPIResponse != null && restAPIResponse.getData() != null) {
                        statusCode.set(restAPIResponse.getHttpStatus());
                    }
                    return statusCode;
                }).block();
        return statusCode.get();
    }
}
