package com.harmoni.pos.http.controller.user;

import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.UserLoginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class AuthController {

    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<RestAPIResponse> authenticateUser(@Valid @RequestBody UserLoginDto userLoginDto) {

        String token = userService.authenticate(userLoginDto);

        log.debug("token generated {} ", token);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(token)
                .httpStatus(HttpStatus.ACCEPTED.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }
}
