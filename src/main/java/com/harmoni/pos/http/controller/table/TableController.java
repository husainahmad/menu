package com.harmoni.pos.http.controller.table;

import com.harmoni.pos.business.service.table.TableService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.TableDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Table entities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/table")
@Slf4j
public class TableController {

    private final TableService tableService;

    /**
     * Creates a new Table.
     *
     * @param tableDto the table data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody TableDto tableDto) {
        int id = tableService.create(tableDto);
        log.debug("Table created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Updates an existing Table.
     *
     * @param id       the table ID
     * @param tableDto the table data transfer object
     * @return ResponseEntity with update status
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody TableDto tableDto) {
        int updated = tableService.update(id, tableDto);
        log.debug("Table updated {} ", updated);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED.value())
                .data(HttpStatus.ACCEPTED)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.ACCEPTED);
    }

    /**
     * Retrieves all Tables.
     *
     * @return ResponseEntity containing the list of tables
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> all() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tableService.list())
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a Table by its ID.
     *
     * @param id the table ID
     * @return ResponseEntity containing the table data
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tableService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Tables by Store ID.
     *
     * @param storeId the store ID
     * @return ResponseEntity containing the list of tables for the store
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<RestAPIResponse> getByStoreId(@PathVariable Integer storeId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(tableService.listByStoreId(storeId))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a Table by its ID.
     *
     * @param id the table ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(tableService.delete(id))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}