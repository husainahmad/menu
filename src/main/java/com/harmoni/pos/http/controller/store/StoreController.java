package com.harmoni.pos.http.controller.store;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.StoreDto;
import com.harmoni.pos.menu.model.dto.TierDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StoreController {

    private final Logger log = LoggerFactory.getLogger(StoreController.class);
    private final StoreService storeService;

    @PostMapping("/store")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreDto storeDto) {
        int id = storeService.create(storeDto);
        log.debug("store created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/store")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.list())
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
