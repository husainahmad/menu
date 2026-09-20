package com.harmoni.pos.http.controller.customization;

import com.harmoni.pos.business.service.customization.CustomizationService;
import com.harmoni.pos.menu.model.dto.CustomizationDto;
import com.harmoni.pos.http.response.RestAPIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Customization entities.
 */
@RestController
@RequestMapping("/api/v1/customization")
@RequiredArgsConstructor
@Slf4j
public class CustomizationController {

    private final CustomizationService customizationService;

    /**
     * Creates a new customization.
     *
     * @param customizationDto the customization DTO
     * @return ResponseEntity with creation result
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@RequestHeader("X-Username") String username, @Valid @RequestBody CustomizationDto customizationDto) {
        int id = customizationService.createCustomization(username, customizationDto.toEntity());
        log.debug("Customization created with ID: {}", id);
        return new ResponseEntity<>(RestAPIResponse.builder().httpStatus(HttpStatus.CREATED.value()).build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves a paginated list of customizations.
     *
     * @param username  the username
     * @param page       the page number
     * @param size       the page size
     * @return ResponseEntity with paginated list
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list(@RequestHeader("X-Username") String username,
                                                @RequestParam(name = "page") int page,
                                                @RequestParam(name = "size") int size) {
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(customizationService.listPaginated(username, page, size))
                        .build(),
                HttpStatus.OK
        );
    }

    /**
     * Retrieves a customization by its ID.
     *
     * @param id the customization ID
     * @return ResponseEntity with customization details
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(customizationService.getCustomizationById(id))
                        .build(),
                HttpStatus.OK
        );
    }

    /**
     * Updates an existing customization, including its options and tier-based prices.
     *
     * @param id the customization ID
     * @param customizationDto the customization DTO
     * @return ResponseEntity with update result
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody CustomizationDto customizationDto) {
        customizationDto.setId(id);
        int rows = customizationService.updateCustomization(customizationDto.toEntity());
        log.debug("Customization updated with ID: {}, rows: {}", id, rows);
        return new ResponseEntity<>(RestAPIResponse.builder().httpStatus(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }

    /**
     * Deletes a customization by its ID.
     *
     * @param id the customization ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        int rows = customizationService.deleteCustomization(id);
        log.debug("Customization deleted with ID: {}, rows: {}", id, rows);
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .build(),
                HttpStatus.OK
        );
    }

    /**
     * Retrieves all customizations for a specific product ID.
     *
     * @param productId the product ID
     * @return ResponseEntity with product's customizations
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<RestAPIResponse> getByProductId(@PathVariable Integer productId) {
        return new ResponseEntity<>(
                RestAPIResponse.builder()
                        .httpStatus(HttpStatus.OK.value())
                        .data(customizationService.getCustomizationById(productId))
                        .build(),
                HttpStatus.OK
        );
    }
}
