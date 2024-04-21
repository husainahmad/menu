package com.harmoni.pos.http.controller.service;

import com.harmoni.pos.business.service.service.ServiceService;
import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import com.harmoni.pos.menu.model.dto.TierDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ServiceController {

    private final Logger log = LoggerFactory.getLogger(ServiceController.class);
    private final ServiceService serviceService;

    @PostMapping("/service")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ServiceDto serviceDto) {

        int id = serviceService.create(serviceDto);

        log.debug("service created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }
}