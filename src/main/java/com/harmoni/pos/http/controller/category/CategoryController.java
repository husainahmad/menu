package com.harmoni.pos.http.controller.category;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import com.harmoni.pos.http.response.RestAPIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/category")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody CategoryDto categoryDto) {
        int id = categoryService.create(categoryDto);
        log.debug("Category created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam(name = "page") int page,
                                                @RequestParam(name = "size") int size) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(categoryService.listPaginated(authHeader, page, size))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.categoryService.get(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.categoryService.delete(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<RestAPIResponse> getByBrandId(@PathVariable Integer brandId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.categoryService.selectByBrandId(brandId))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/tier")
    public ResponseEntity<RestAPIResponse> getAll(@RequestHeader("Authorization") String authHeader) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(categoryService.getListByUserAuth(authHeader))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
