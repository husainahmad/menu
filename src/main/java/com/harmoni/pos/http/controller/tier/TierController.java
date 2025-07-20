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

/**
 * REST controller for managing Tier entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/tier")
public class TierController {

    private final TierService tierService;

    /**
     * Creates a new Tier.
     *
     * @param tierDto the tier data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TierDto tierDto) {
        int id = tierService.create(tierDto);
        log.debug("tier created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves a Tier by its ID.
     *
     * @param id the tier ID
     * @return ResponseEntity containing the tier data
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates an existing Tier.
     *
     * @param tierDto the tier data transfer object
     * @param id the tier ID
     * @return ResponseEntity with update status
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@Valid @RequestBody TierDto tierDto, @PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.update(tierDto, id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Tiers by Brand ID.
     *
     * @param brandId the brand ID
     * @return ResponseEntity containing tiers for the brand
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<RestAPIResponse> getByBrand(@PathVariable Integer brandId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.getByBrandId(brandId))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a Tier by its ID.
     *
     * @param id the tier ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> deleteByBrand(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Tiers by Brand ID and Tier Type.
     *
     * @param brandId the brand ID
     * @param tierType the tier type
     * @return ResponseEntity containing tiers for the brand and type
     */
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
