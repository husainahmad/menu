package com.harmoni.pos.http.controller.kitchen;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.kitchen.KitchenRoutingService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenCategoryRoutingDto;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenProductRoutingDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category and product routing endpoints, scoped to a store.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@PreAuthorize("hasAnyAuthority('availability:view', 'operation:read', 'rbac:manage')")
public class KitchenRoutingController {

    private final KitchenRoutingService kitchenRoutingService;

    @GetMapping("/api/v1/stores/{storeId}/kitchen-routings")
    public ResponseEntity<RestAPIResponse> list(@PathVariable Integer storeId) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenRoutingService.listByStore(storeId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/v1/stores/{storeId}/kitchen-routings/categories")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> routeCategory(
            @PathVariable Integer storeId,
            @Valid @RequestBody KitchenCategoryRoutingDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenRoutingService.routeCategory(storeId, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/v1/stores/{storeId}/kitchen-routings/products")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> routeProduct(
            @PathVariable Integer storeId,
            @Valid @RequestBody KitchenProductRoutingDto dto) {
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(kitchenRoutingService.routeProduct(storeId, dto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/stores/{storeId}/kitchen-routings/categories/{categoryId}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> deleteCategory(@PathVariable Integer storeId,
                                                          @PathVariable Integer categoryId) {
        kitchenRoutingService.deleteCategoryRouting(storeId, categoryId);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/stores/{storeId}/kitchen-routings/products/{productId}")
    @PreAuthorize("hasAnyAuthority('availability:edit', 'operation:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> deleteProduct(@PathVariable Integer storeId,
                                                         @PathVariable Integer productId) {
        kitchenRoutingService.deleteProductRouting(storeId, productId);
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
