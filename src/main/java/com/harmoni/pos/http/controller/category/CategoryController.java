package com.harmoni.pos.http.controller.category;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import com.harmoni.pos.http.response.RestAPIResponse;
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
public class CategoryController {

    private final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<RestAPIResponse> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        int id = categoryService.create(categoryDto);

        log.debug("Category created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/category")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(categoryService.list())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.categoryService.get(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/category/brand/{brandId}")
    public ResponseEntity<RestAPIResponse> getByBrandId(@PathVariable Integer brandId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.categoryService.selectByBrandId(brandId))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
