package com.harmoni.pos.http.controller.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.service.ServiceService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Service entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/service")
@PreAuthorize("hasAnyAuthority('terminal:view', 'operation:read', 'rbac:manage')")
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Creates a new Service.
     *
     * @param serviceDto the service data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ServiceDto serviceDto) {

        serviceService.create(serviceDto);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing Service.
     *
     * @param id         the service ID
     * @param serviceDto the service data transfer object
     * @return ResponseEntity with the update status
     */
    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody ServiceDto serviceDto) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.update(id, serviceDto))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a Service by its ID.
     *
     * @param id the service ID
     * @return ResponseEntity with the delete status
     */
    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves all Services with their sub-services.
     *
     * @return ResponseEntity containing all services
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> get() {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.getAllWithSub())
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves one page of Services with their sub-services.
     *
     * @param page the page number, 1-based
     * @param size the number of services per page
     * @return ResponseEntity containing the paginated services
     */
    @GetMapping("/page")
    public ResponseEntity<RestAPIResponse> listPaginated(@RequestParam(name = "page") int page,
                                                         @RequestParam(name = "size") int size) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(serviceService.listPaginated(page, size))
                .httpStatus(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
