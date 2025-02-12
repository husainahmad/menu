package com.harmoni.pos.http.controller.tier;

import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/tier")
public class TierController {

    private final TierService tierService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TierDto tierDto) {

        int id = tierService.create(tierDto);
        log.debug("tier created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@Valid @RequestBody TierDto tierDto, @PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.update(tierDto, id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<RestAPIResponse> getByBrand(@PathVariable Integer brandId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.getByBrandId(brandId))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> deleteByBrand(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/brand/{brandId}/type/{tierType}")
    public ResponseEntity<RestAPIResponse> getByBrandType(@PathVariable Integer brandId,
                                                          @PathVariable TierType tierType) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.getByBrandIdAndTierType(brandId, tierType))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
