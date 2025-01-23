package com.harmoni.pos.http.controller.store;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.StoreDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/store")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreDto storeDto) {
        int id = storeService.create(storeDto);
        log.debug("store created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/store")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.getAllStoresByBrandId(24L))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @DeleteMapping("/store/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Long id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @PutMapping("/store/{id}")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Long id, @Valid @RequestBody StoreDto storeDto) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.update(id, storeDto))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
