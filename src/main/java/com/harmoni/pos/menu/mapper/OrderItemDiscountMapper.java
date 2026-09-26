package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.OrderItemDiscount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for OrderItemDiscount entity database operations.
 */
@Mapper
public interface OrderItemDiscountMapper {

    /**
     * Deletes an OrderItemDiscount by its primary key.
     *
     * @param id the OrderItemDiscount ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every discount recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @return number of rows affected
     */
    int deleteByOrderItemId(Long orderItemId);

    /**
     * Inserts a new OrderItemDiscount.
     *
     * @param row the OrderItemDiscount object
     * @return number of rows affected
     */
    int insert(OrderItemDiscount row);

    /**
     * Inserts several discounts in a single statement.
     *
     * @param rows the OrderItemDiscount objects
     * @return number of rows affected
     */
    int insertBatch(@Param("orderItemDiscounts") List<OrderItemDiscount> rows);

    /**
     * Selects an OrderItemDiscount by its primary key.
     *
     * @param id the OrderItemDiscount ID
     * @return the OrderItemDiscount object
     */
    OrderItemDiscount selectByPrimaryKey(Long id);

    /**
     * Selects every discount recorded against an order line, oldest first.
     *
     * @param orderItemId the order line ID
     * @return list of OrderItemDiscount objects
     */
    List<OrderItemDiscount> selectByOrderItemId(Long orderItemId);

    /**
     * Selects every discount produced by a promotion.
     *
     * @param promotionId the Promotion ID
     * @return list of OrderItemDiscount objects
     */
    List<OrderItemDiscount> selectByPromotionId(Long promotionId);

    /**
     * Aggregates the discount granted per order line for a promotion, optionally
     * restricted to a date range. Powers promotion redemption reporting.
     *
     * @param promotionId the Promotion ID
     * @param from        optional inclusive lower bound on created_at
     * @param to          optional inclusive upper bound on created_at
     * @return list of rows keyed by orderItemId with the summed discountAmount
     */
    List<OrderItemDiscount> selectRedemptionSummary(@Param("promotionId") Long promotionId,
                                                     @Param("from") java.util.Date from,
                                                     @Param("to") java.util.Date to);

    /**
     * Selects every discount of a given type recorded against an order line.
     *
     * @param orderItemId the order line ID
     * @param discountType the discount type
     * @return list of OrderItemDiscount objects
     */
    List<OrderItemDiscount> selectByOrderItemIdAndType(@Param("orderItemId") Long orderItemId,
                                                       @Param("discountType") DiscountType discountType);
}
