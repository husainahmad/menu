package com.harmoni.pos.http.controller.sku;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.SkuDto;
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
public class SkuController {

    private final Logger log = LoggerFactory.getLogger(SkuController.class);
    private final SkuService skuService;

    @PostMapping("/sku")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SkuDto skuDto) {
        int id = skuService.create(skuDto);

        log.debug("Sku created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/sku")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(skuService.selectAll())
                .httpStatus(HttpStatus.OK.value())
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }


}
