package com.harmoni.pos.http.controller.storeservicetype;

import com.harmoni.pos.business.service.storeservicetype.StoreServiceTypeService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
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
public class StoreServiceTypeController {

    private final Logger log = LoggerFactory.getLogger(StoreServiceTypeController.class);
    private final StoreServiceTypeService storeServiceTypeService;

    @PostMapping("/storeservicetype")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreServiceTypeDto storeServiceTypeDto) {

        int id = storeServiceTypeService.create(storeServiceTypeDto);

        log.debug("store service type created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }
}
