package com.harmoni.pos.http.controller.promotion;

import com.harmoni.pos.business.service.promotion.orderitemdiscount.OrderItemDiscountService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.OrderItemDiscountDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * REST controller for recording and reading the discounts applied to order lines.
 * <p>
 * Intended for the order service to call when it confirms a basket, so a historic
 * order line always carries the discount that was actually granted.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order-item-discount")
@Slf4j
public class OrderItemDiscountController {

    private final OrderItemDiscountService orderItemDiscountService;

    /**
     * Records a single discount against an order line.
     *
     * @param orderItemDiscountDto the discount to record
     * @return the persisted discount
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody OrderItemDiscountDto orderItemDiscountDto) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(orderItemDiscountService.create(orderItemDiscountDto))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    /**
     * Records several discounts against order lines in one call.
     *
     * @param orderItemDiscountDtos the discounts to record
     * @return the persisted discounts
     */
    @PostMapping("/bulk")
    public ResponseEntity<RestAPIResponse> createBulk(
            @Valid @RequestBody List<OrderItemDiscountDto> orderItemDiscountDtos) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(orderItemDiscountService.createBulk(orderItemDiscountDtos))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieves every discount recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @return list of discounts, oldest first
     */
    @GetMapping("/order-item/{orderItemId:\\d+}")
    public ResponseEntity<RestAPIResponse> getByOrderItemId(@PathVariable("orderItemId") Long orderItemId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(orderItemDiscountService.getByOrderItemId(orderItemId))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves the total discount granted per order line by a promotion.
     *
     * @param promotionId the promotion ID
     * @param from        optional inclusive lower bound, ISO-8601 date time
     * @param to          optional inclusive upper bound, ISO-8601 date time
     * @return list of discounts carrying the summed amount per order line
     */
    @GetMapping("/promotion/{promotionId:\\d+}/summary")
    public ResponseEntity<RestAPIResponse> getRedemptionSummary(
            @PathVariable("promotionId") Long promotionId,
            @RequestParam(name = "from", required = false) Date from,
            @RequestParam(name = "to", required = false) Date to) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(orderItemDiscountService.getRedemptionSummary(promotionId, from, to))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes every discount recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @return the number of deleted rows
     */
    @DeleteMapping("/order-item/{orderItemId:\\d+}")
    public ResponseEntity<RestAPIResponse> deleteByOrderItemId(@PathVariable("orderItemId") Long orderItemId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(orderItemDiscountService.deleteByOrderItemId(orderItemId))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
