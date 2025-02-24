package com.harmoni.pos.http.controller.sku;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
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
@RequestMapping("/api/v1/sku")

public class SkuController {

    private final SkuService skuService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuAddDto skuDto) {
        int id = skuService.create(skuDto);
        log.debug("Sku created {} ", id);
        return new ResponseEntity<>(RestAPIResponse
                .builder().httpStatus(HttpStatus.CREATED.value()).build(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        skuService.deleteSku(id);

        log.debug("Sku deleted {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.NO_CONTENT.value()).build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/price")
    public ResponseEntity<RestAPIResponse> getDetails(@RequestHeader("Authorization") String authHeader,
                                                      @RequestParam List<Integer> ids) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(skuService.selectPriceByIds(authHeader.substring(7), ids))
                .httpStatus(HttpStatus.NO_CONTENT.value()).build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
