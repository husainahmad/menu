package com.harmoni.pos.http.controller.kitchen;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.kitchen.KitchenPrinterService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenPrinterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kitchen printer configuration endpoints.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@PreAuthorize("hasAnyAuthority('availability:view', 'operation:read', 'rbac:manage')")
public class KitchenPrinterController {

    private final KitchenPrinterService kitchenPrinterService;

    @GetMapping("/api/v1/kitchen-stations/{stationId}/printers")
    public ResponseEntity<RestAPIResponse> list(@PathVariable Integer stationId) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenPrinterService.listByStation(stationId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/v1/kitchen-stations/{stationId}/printers")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@PathVariable Integer stationId,
                                                  @Valid @RequestBody KitchenPrinterDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(kitchenPrinterService.create(stationId, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/kitchen-printers/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenPrinterService.get(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/api/v1/kitchen-printers/{id}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody KitchenPrinterDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenPrinterService.update(id, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/kitchen-printers/{id}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        kitchenPrinterService.delete(id);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
