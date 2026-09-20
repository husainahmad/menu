package com.harmoni.pos.http.controller.product;

import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationConfigDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationReplaceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the customizations assigned to a product.
 *
 * <p>Operations are scoped under {@code /api/v1/product/{productId}/customization}:
 * <ul>
 *     <li>GET  — list the product's customizations with effective values, options and tier prices</li>
 *     <li>PUT  — (re)assign the ordered list of customizations</li>
 *     <li>PUT  /{linkId} — configure per-product overrides (required / min / max, sort order)</li>
 *     <li>DELETE /{linkId} — detach a customization (removes only the link)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/product/{productId}/customization")
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationController {

    private final ProductCustomizationService productCustomizationService;

    /**
     * Retrieves the customizations assigned to a product.
     *
     * @param productId the product ID
     * @return ordered list of product customization details
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list(@PathVariable Integer productId) {
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(productCustomizationService.getDetailedByProductId(productId))
                        .error(null)
                        .build(),
                HttpStatus.OK);
    }

    /**
     * Replaces the customizations assigned to a product.
     *
     * @param productId                   the product ID
     * @param productCustomizationReplace the ordered customization IDs to assign
     * @return response with replacement status
     */
    @PutMapping("")
    public ResponseEntity<RestAPIResponse> replace(@PathVariable Integer productId,
                                                   @Valid @RequestBody ProductCustomizationReplaceDto productCustomizationReplace) {
        int rows = productCustomizationService.replaceForProduct(productId,
                productCustomizationReplace.getCustomizationIds());
        log.debug("Replaced product customizations productId={}, rows={}", productId, rows);
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(null)
                        .error(null)
                        .build(),
                HttpStatus.OK);
    }

    /**
     * Configures the per-product overrides of a single product-customization link.
     *
     * @param productId the product ID
     * @param linkId    the product-customization link ID
     * @param config    the override configuration
     * @return response with update status
     */
    @PutMapping("/{linkId:\\d+}")
    public ResponseEntity<RestAPIResponse> configure(@PathVariable Integer productId,
                                                     @PathVariable Integer linkId,
                                                     @Valid @RequestBody ProductCustomizationConfigDto config) {
        int rows = productCustomizationService.updateConfiguration(linkId, config);
        log.debug("Configured product customization productId={}, linkId={}, rows={}", productId, linkId, rows);
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(null)
                        .error(null)
                        .build(),
                HttpStatus.OK);
    }

    /**
     * Detaches a customization from a product by removing only the join link.
     *
     * @param productId the product ID
     * @param linkId    the product-customization link ID
     * @return response with deletion status
     */
    @DeleteMapping("/{linkId:\\d+}")
    public ResponseEntity<RestAPIResponse> detach(@PathVariable Integer productId,
                                                  @PathVariable Integer linkId) {
        int rows = productCustomizationService.delete(linkId);
        log.debug("Detached customization productId={}, linkId={}, rows={}", productId, linkId, rows);
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(null)
                        .error(null)
                        .build(),
                HttpStatus.OK);
    }
}