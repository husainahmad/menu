package com.harmoni.pos.http.controller.device;

import com.harmoni.pos.business.service.device.DeviceService;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.DeviceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Device endpoints, scoped to a store.
 *
 * <p>Devices are plain configuration records: a name, a hardware type, an
 * operating mode and a mode-specific JSON configuration. They carry no
 * credential and no runtime identity.</p>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@PreAuthorize("hasAnyAuthority('terminal:view', 'operation:read', 'rbac:manage')")
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * Lists the devices of a store.
     *
     * @param storeId the store id
     * @return the store's devices
     */
    @GetMapping("/api/v1/stores/{storeId}/devices")
    public ResponseEntity<RestAPIResponse> list(@PathVariable Integer storeId) {
        CallerScopeContext.current().requireStore(storeId);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(deviceService.listByStore(storeId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Registers a device under a store.
     *
     * @param storeId the store id
     * @param dto     the device payload
     * @return the created device
     */
    @PostMapping("/api/v1/stores/{storeId}/devices")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@PathVariable Integer storeId,
                                                  @Valid @RequestBody DeviceDto dto) {
        CallerScopeContext.current().requireStore(storeId);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(deviceService.create(storeId, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/devices/configuration")
    public ResponseEntity<RestAPIResponse> getConfiguration(@RequestParam("address") String address) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(deviceService.getConfigurationByAddress(address))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Returns one device.
     *
     * @param id the device id
     * @return the device
     */
    @GetMapping("/api/v1/devices/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(deviceService.get(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates a device.
     *
     * @param id  the device id
     * @param dto the device payload
     * @return the updated device
     */
    @PutMapping("/api/v1/devices/{id}")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody DeviceDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(deviceService.update(id, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a device.
     *
     * @param id the device id
     * @return {@code true} when deleted
     */
    @DeleteMapping("/api/v1/devices/{id}")
    @PreAuthorize("hasAnyAuthority('terminal:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        deviceService.delete(id);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
