package com.harmoni.pos.http.controller.tier;

import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.http.response.RestAPIResponse;
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
public class TierController {

    private final Logger log = LoggerFactory.getLogger(TierController.class);
    private final TierService tierService;

    @PostMapping("/tier")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TierDto tierDto) {

        int id = tierService.create(tierDto);
        log.debug("tier created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/tier")
    public ResponseEntity<RestAPIResponse> list() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.list())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/tier/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/tier/brand/{brandId}")
    public ResponseEntity<RestAPIResponse> getByBrand(@PathVariable Integer brandId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.getByBrandId(brandId))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
