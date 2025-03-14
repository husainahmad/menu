package com.harmoni.pos.http.controller.chain;

import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ChainDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chain")
@Slf4j
public class ChainController {

    private final ChainService chainService;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ChainDto chainDto) {
        int id = chainService.create(chainDto);
        log.debug("Chain created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@Valid @RequestBody ChainDto chainDto, @PathVariable Integer id) {
        chainService.update(chainDto, id);
        log.debug("Chain updated {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED.value())
                .data(HttpStatus.ACCEPTED)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.get(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.delete(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.list())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/brand/{id}")
    public ResponseEntity<RestAPIResponse> listByBrandId(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.listByBrandId(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
