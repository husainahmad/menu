package com.harmoni.pos.http.controller.skutierprice;

import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing SKU Tier Price entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/skutier")
public class SkuTierPriceController {

    private final SkuTierPriceService skuTierPriceService;

    /**
     * Creates a new SKU Tier Price.
     *
     * @param skuTierPriceDto the SKU Tier Price data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuTierPriceDto skuTierPriceDto) {
        int id = skuTierPriceService.create(skuTierPriceDto);
        log.debug("Sku Tier Price created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves SKU Tier Prices by SKU IDs and Tier ID.
     *
     * @param skuIds the list of SKU IDs
     * @param tierId the tier ID
     * @return ResponseEntity containing SKU Tier Prices
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> listBySku(@RequestParam List<Integer> skuIds,
                                                     @RequestParam Integer tierId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(skuTierPriceService.selectBySkusTierId(skuIds, tierId))
                .httpStatus(HttpStatus.OK.value())
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
