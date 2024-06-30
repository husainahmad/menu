package com.harmoni.pos.http.controller.sku;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.SkuDto;
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

public class SkuController {

    private final SkuService skuService;

    @PostMapping("/sku")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuDto skuDto) {
        int id = skuService.create(skuDto);

        log.debug("Sku created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse
                .builder().httpStatus(HttpStatus.CREATED.value()).build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/sku/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        skuService.deleteSku(id);

        log.debug("Sku deleted {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.NO_CONTENT.value()).build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.NO_CONTENT);
    }
}
