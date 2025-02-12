package com.harmoni.pos.http.controller.brand;

import com.harmoni.pos.business.service.brand.BrandService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.BrandDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brand")
public class BrandController {

    private final Logger log = LoggerFactory.getLogger(BrandController.class);
    private final BrandService brandService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody BrandDto brandDto) {
        int id = brandService.create(brandDto);
        log.debug("Brand created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(),HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<RestAPIResponse> all() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(brandService.list())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse,HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(brandService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse,HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        brandService.delete(id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(null)
                .build();

        return new ResponseEntity<>(restAPIResponse,HttpStatus.OK);

    }
}
