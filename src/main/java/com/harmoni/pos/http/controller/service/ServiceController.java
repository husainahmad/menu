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

/**
 * REST controller for managing Service entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/service")
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Creates a new Service.
     *
     * @param serviceDto the service data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ServiceDto serviceDto) {

        serviceService.create(serviceDto);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieves all Services with their sub-services.
     *
     * @return ResponseEntity containing all services
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> get() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.getAllWithSub())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
