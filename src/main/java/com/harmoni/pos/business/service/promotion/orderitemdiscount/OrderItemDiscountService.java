package com.harmoni.pos.business.service.promotion.orderitemdiscount;

import com.harmoni.pos.menu.model.OrderItemDiscount;
import com.harmoni.pos.menu.model.dto.OrderItemDiscountDto;

import java.util.Date;
import java.util.List;

/**
 * Business service for recording and reading the discounts applied to order lines.
 */
public interface OrderItemDiscountService {

    /**
     * Records a single discount against an order line. When a promotion is
     * referenced its code and name are copied onto the row so the snapshot stays
     * readable after the promotion changes.
     *
     * @param orderItemDiscountDto the DTO carrying the discount
     * @return the persisted discount with its generated id and timestamp
     */
    OrderItemDiscount create(OrderItemDiscountDto orderItemDiscountDto);

    /**
     * Records several discounts against order lines in one transaction.
     *
     * @param orderItemDiscountDtos the DTOs carrying the discounts
     * @return the persisted discounts
     */
    List<OrderItemDiscount> createBulk(List<OrderItemDiscountDto> orderItemDiscountDtos);

    /**
     * Retrieves every discount recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @return list of discounts, oldest first
     */
    List<OrderItemDiscount> getByOrderItemId(Long orderItemId);

    /**
     * Retrieves the total discount granted per order line by a promotion.
     *
     * @param promotionId the promotion ID
     * @param from        optional inclusive lower bound on the snapshot timestamp
     * @param to          optional inclusive upper bound on the snapshot timestamp
     * @return list of discounts carrying the summed amount per order line
     */
    List<OrderItemDiscount> getRedemptionSummary(Long promotionId, Date from, Date to);

    /**
     * Deletes every discount recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @return the number of deleted rows
     */
    int deleteByOrderItemId(Long orderItemId);
}
