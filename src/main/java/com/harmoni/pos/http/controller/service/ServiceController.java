package com.harmoni.pos.http.controller.service;

import com.harmoni.pos.business.service.service.ServiceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping("/service")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ServiceDto serviceDto) {

        int id = serviceService.create(serviceDto);

        log.debug("service created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/service")
    public ResponseEntity<RestAPIResponse> get() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.getAllWithSub())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
