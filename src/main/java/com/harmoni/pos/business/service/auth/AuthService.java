package com.harmoni.pos.business.service.auth;

import com.harmoni.pos.business.service.rest.RestClientService;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements Serializable {

    private final RestClientService restClientService;

    @Value("${auth.url.register}")
    private String url;

    @Value("${auth.url.delete}")
    private String urlDelete;

    @Value("${auth.url.update}")
    private String urlUpdate;



    public int create(String token, UserDto userDto) {
        AtomicInteger statusCode = new AtomicInteger();
        restClientService.post(token, url, Mono.just(userDto), UserDto.class).map(restAPIResponse -> {
            if (restAPIResponse!=null && restAPIResponse.getData()!=null) {
                statusCode.set(restAPIResponse.getHttpStatus());
            }
            return statusCode;
        }).block();
        return statusCode.get();
    }

    public int delete(String token, String username) {
        AtomicInteger statusCode = new AtomicInteger();
        restClientService.delete(token, urlDelete.concat("/").concat(username)).map(restAPIResponse -> {
            if (restAPIResponse!=null && restAPIResponse.getData()!=null) {
                statusCode.set(restAPIResponse.getHttpStatus());
            }
            return statusCode;
        }).block();
        return statusCode.get();
    }

    public int update(String token, UserEditDto userEditDto) {
        AtomicInteger statusCode = new AtomicInteger();
        restClientService.put(token, urlUpdate.concat("/").concat(userEditDto.getUsername()), Mono.just(userEditDto), UserEditDto.class)
                .map(restAPIResponse -> {
            if (restAPIResponse!=null && restAPIResponse.getData()!=null) {
                statusCode.set(restAPIResponse.getHttpStatus());
            }
            return statusCode;
        }).block();
        return statusCode.get();
    }

}
