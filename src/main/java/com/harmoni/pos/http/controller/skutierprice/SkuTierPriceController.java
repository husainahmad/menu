package com.harmoni.pos.http.controller.skutierprice;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.SkuDto;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class SkuTierPriceController {

    private final Logger log = LoggerFactory.getLogger(SkuTierPriceController.class);
    private final SkuTierPriceService skuTierPriceService;

    @PostMapping("/skutier")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuTierPriceDto skuTierPriceDto) {
        int id = skuTierPriceService.create(skuTierPriceDto);

        log.debug("Sku Tier Price created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/skutier")
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
