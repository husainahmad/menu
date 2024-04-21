package com.harmoni.pos.http.controller.chain;

import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ChainDto;
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
public class ChainController {

    private final Logger log = LoggerFactory.getLogger(ChainController.class);
    private final ChainService chainService;

    @PostMapping("/chain")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ChainDto chainDto) {
        int id = chainService.create(chainDto);
        log.debug("Chain created {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @PutMapping("/chain/{id}")
    public ResponseEntity<RestAPIResponse> update(@Valid @RequestBody ChainDto chainDto, @PathVariable Long id) {
        chainService.update(chainDto, id);
        log.debug("Chain updated {} ", id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED.value())
                .data(HttpStatus.ACCEPTED)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("/chain/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Long id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.get(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @GetMapping("/chain")
    public ResponseEntity<RestAPIResponse> list() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(chainService.list())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
