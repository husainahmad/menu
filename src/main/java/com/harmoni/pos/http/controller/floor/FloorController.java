package com.harmoni.pos.http.controller.floor;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.floor.FloorService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.FloorDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Floor entities: the floors of a store, each
 * grouping a set of tables.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/floor")
@Slf4j
@PreAuthorize("hasAnyAuthority('table:view', 'operation:read', 'rbac:manage')")
public class FloorController {

    private final FloorService floorService;

    /**
     * Creates a new Floor.
     *
     * @param floorDto the floor data
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('table:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody FloorDto floorDto) {
        int id = floorService.create(floorDto);
        log.debug("Floor created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Updates an existing Floor.
     *
     * @param id       the floor ID
     * @param floorDto the floor data
     * @return ResponseEntity with update status
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('table:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody FloorDto floorDto) {
        int updated = floorService.update(id, floorDto);
        log.debug("Floor updated {} ", updated);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED.value())
                .data(HttpStatus.ACCEPTED)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }

    /**
     * Retrieves a Floor by its ID.
     *
     * @param id the floor ID
     * @return ResponseEntity containing the floor
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(floorService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves the Floors of a store.
     *
     * @param storeId the store ID
     * @return ResponseEntity containing the floors of the store
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<RestAPIResponse> getByStoreId(@PathVariable Integer storeId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(floorService.listByStoreId(storeId))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Applies a new ordering to the given floors.
     *
     * @param floors floors carrying id and sort order
     * @return ResponseEntity with update status
     */
    @PutMapping("/reorder")
    @PreAuthorize("hasAnyAuthority('table:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> reorder(@RequestBody List<FloorDto> floors) {
        int updated = floorService.reorder(floors);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED.value())
                .data(updated)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }

    /**
     * Deletes a Floor by its ID. Refused while it still has tables.
     *
     * @param id the floor ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('table:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(floorService.delete(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
