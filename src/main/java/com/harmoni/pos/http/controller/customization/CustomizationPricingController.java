package com.harmoni.pos.http.controller.customization;

import com.harmoni.pos.business.service.customization.CustomizationPricingService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for pricing the customization choices on a sale line.
 *
 * @author husainahmad
 */
@RestController
@RequestMapping("/api/v1/customization")
@RequiredArgsConstructor
@Slf4j
public class CustomizationPricingController {

    private final CustomizationPricingService customizationPricingService;

    /**
     * Prices the customization choices on one order line.
     * <p>
     * This is what an order service calls when a customer confirms a basket containing a
     * customised item. The caller names the chosen options and nothing else: the price
     * tier is taken from the operator's store, so the caller cannot decide what an option
     * costs. Nothing is persisted; the caller stores what comes back.
     *
     * @param request  the product, SKU, quantity and chosen option IDs
     * @param username the operator whose store supplies the price tier
     * @return each chosen option priced and named, plus the total surcharge for the line
     */
    @PostMapping("/price")
    public ResponseEntity<RestAPIResponse> price(
            @RequestHeader("X-Username") String username,
            @RequestBody CustomizationPriceRequestDto request) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(customizationPricingService.price(request, username))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}