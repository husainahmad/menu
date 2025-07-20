package com.harmoni.pos.http.controller.storeservicetype;

import com.harmoni.pos.business.service.storeservicetype.StoreServiceTypeService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Store Service Type entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/storeservicetype")
public class StoreServiceTypeController {

    private final StoreServiceTypeService storeServiceTypeService;

    /**
     * Creates a new Store Service Type.
     *
     * @param storeServiceTypeDto the store service type data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreServiceTypeDto storeServiceTypeDto) {
        int id = storeServiceTypeService.create(storeServiceTypeDto);
        log.debug("store service type created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }
}
