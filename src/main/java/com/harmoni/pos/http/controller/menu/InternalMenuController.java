package com.harmoni.pos.http.controller.menu;

import com.harmoni.pos.business.service.validation.OrderItemValidationService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The menu service's answer to the order service's question.
 * <p>
 * These endpoints are for other services, not for the POS client. A client holding a basket
 * across a shift is holding a description of the menu from whenever the basket was started,
 * and nothing in the request can be trusted to still describe what the shop sells. The order
 * service asks here immediately before it creates the order, and the answer it gets back is
 * the catalogue's, not the client's.
 *
 * @author husainahmad
 */
@RestController
@RequestMapping("/internal/v1/menu")
@RequiredArgsConstructor
@Slf4j
public class InternalMenuController {

    private final OrderItemValidationService orderItemValidationService;

    /**
     * Confirms the lines of a basket and returns the official menu data for them.
     * <p>
     * Nothing is persisted and no total is produced. The caller multiplies out the unit
     * prices against the quantities it sent, applies its own promotions, and stores what
     * comes back; the names and prices in the response are what a receipt printed next year
     * must show, which is why they are read here rather than at print time.
     * <p>
     * A basket that cannot be sold is refused outright with the reason, rather than
     * answered with a flag: an order service cannot create an order from half a basket
     * without guessing which half was meant.
     *
     * @param request the store, and the product, SKU, quantity and customization choices
     *                per line
     * @return each line confirmed, named and priced
     */
    @PostMapping("/validate-order-items")
    public ResponseEntity<RestAPIResponse> validateOrderItems(@Valid @RequestBody ValidateOrderItemsRequest request) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(orderItemValidationService.validate(request))
                .error(null)
                .build();
        log.debug("Validated {} order items for store {}", request.getItems().size(), request.getStoreId());
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}