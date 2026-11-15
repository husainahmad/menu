package com.harmoni.pos.http.controller.kitchen;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.kitchen.KitchenStationService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenStationDto;
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
 * Kitchen station endpoints, scoped to a store.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@PreAuthorize("hasAnyAuthority('availability:view', 'operation:read', 'rbac:manage')")
public class KitchenStationController {

    private final KitchenStationService kitchenStationService;

    @GetMapping("/api/v1/stores/{storeId}/kitchen-stations")
    public ResponseEntity<RestAPIResponse> list(@PathVariable Integer storeId) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenStationService.listByStore(storeId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/v1/stores/{storeId}/kitchen-stations")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@PathVariable Integer storeId,
                                                  @Valid @RequestBody KitchenStationDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(kitchenStationService.create(storeId, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/stores/{storeId}/kitchen-stations/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer storeId,
                                               @PathVariable Integer id) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenStationService.get(storeId, id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/api/v1/kitchen-stations/{id}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody KitchenStationDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenStationService.update(id, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/kitchen-stations/{id}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        kitchenStationService.delete(id);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
