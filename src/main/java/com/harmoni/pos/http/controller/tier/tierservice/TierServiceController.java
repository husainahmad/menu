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

import java.util.List;

/**
 * REST controller for managing Tier Service entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/tier")
public class TierServiceController {

    private final TierServiceService tierServiceService;

    /**
     * Creates a new Tier Service.
     *
     * @param tierServiceDto the tier service data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("/service")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TierServiceDto tierServiceDto) {
        int id = tierServiceService.create(tierServiceDto);
        log.debug("tier created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves Tier Services by Brand ID.
     *
     * @param id the brand ID
     * @return ResponseEntity containing tier services for the brand
     */
    @GetMapping("/service")
    public ResponseEntity<RestAPIResponse> getByBrand(@RequestParam(name = "brandId") Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierServiceService.getByBrandId(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates Tier Services for a Tier.
     *
     * @param tierServiceDtos the list of tier service data transfer objects
     * @param tierId the tier ID
     * @return ResponseEntity with update status
     */
    @PutMapping("/{tierId}/service")
    public ResponseEntity<RestAPIResponse> update(@RequestBody List<TierServiceDto> tierServiceDtos,
                                               @Valid @PathVariable Integer tierId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tierServiceService.updateTierServices(tierServiceDtos, tierId))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
