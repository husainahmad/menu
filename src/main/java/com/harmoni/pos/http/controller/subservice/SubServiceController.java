package com.harmoni.pos.http.controller.subservice;

import com.harmoni.pos.business.service.subservice.SubServiceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing SubService entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/subservice")
public class SubServiceController {

    private final SubServiceService subServiceService;

    /**
     * Creates a new SubService.
     *
     * @param subServiceDto the subservice data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SubServiceDto subServiceDto) {
        int id = subServiceService.create(subServiceDto);
        log.debug("subservice created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }
}
