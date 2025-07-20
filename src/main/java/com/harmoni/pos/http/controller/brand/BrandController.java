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

/**
 * REST controller for managing Brand entities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brand")
public class BrandController {

    private final Logger log = LoggerFactory.getLogger(BrandController.class);
    private final BrandService brandService;

    /**
     * Creates a new Brand.
     *
     * @param brandDto the brand data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody BrandDto brandDto) {
        int id = brandService.create(brandDto);
        log.debug("Brand created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(),HttpStatus.CREATED);
    }

    /**
     * Retrieves all Brands.
     *
     * @return ResponseEntity containing the list of brands
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> all() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(brandService.list())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse,HttpStatus.OK);

    }

    /**
     * Retrieves a Brand by its ID.
     *
     * @param id the brand ID
     * @return ResponseEntity containing the brand data
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(brandService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse,HttpStatus.OK);

    }

    /**
     * Deletes a Brand by its ID.
     *
     * @param id the brand ID
     * @return ResponseEntity with deletion status
     */
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
