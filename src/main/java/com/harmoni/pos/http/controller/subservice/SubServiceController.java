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

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class SubServiceController {

    private final SubServiceService subServiceService;

    @PostMapping("/subservice")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody SubServiceDto subServiceDto) {

        int id = subServiceService.create(subServiceDto);

        log.debug("subservice created {} ", id);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }
}
