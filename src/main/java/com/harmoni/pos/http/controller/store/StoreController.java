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
@RequestMapping("/api/v1/store")
@Slf4j
public class StoreController {

    private final StoreService storeService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreDto storeDto) {
        int id = storeService.create(storeDto);
        log.debug("store created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list(@RequestParam(name = "chainId") Integer chainId,
                                                @RequestParam(name = "page") int page,
                                                @RequestParam(name = "size") int size,
                                                @RequestParam(name = "search") String search) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.getAllStoresByChainIdPaginated(chainId, page, size, search))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id, @Valid @RequestBody StoreDto storeDto) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.update(id, storeDto))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
