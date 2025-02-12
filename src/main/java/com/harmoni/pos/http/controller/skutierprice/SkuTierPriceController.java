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

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/skutier")
public class SkuTierPriceController {

    private final SkuTierPriceService skuTierPriceService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuTierPriceDto skuTierPriceDto) {
        int id = skuTierPriceService.create(skuTierPriceDto);
        log.debug("Sku Tier Price created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

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
