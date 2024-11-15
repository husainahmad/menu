package com.harmoni.pos.http.controller.tier.tierservice;

import com.harmoni.pos.business.service.tier.tierservice.TierServiceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.TierServiceDto;
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
public class TierServiceController {

    private final TierServiceService tierServiceService;

    @PostMapping("/tier/service")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TierServiceDto tierServiceDto) {

        int id = tierServiceService.create(tierServiceDto);
        log.debug("tier created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/tier/service/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierServiceService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/tier/service/brand/{id}")
    public ResponseEntity<RestAPIResponse> getByBrand(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierServiceService.getByBrandId(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @PutMapping("/tier/service/{id}")
    public ResponseEntity<RestAPIResponse> get(@Valid @RequestBody TierServiceDto tierServiceDto, @PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierServiceService.update(tierServiceDto, id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
