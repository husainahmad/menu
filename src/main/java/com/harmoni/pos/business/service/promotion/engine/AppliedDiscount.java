package com.harmoni.pos.business.service.promotion.engine;

import com.harmoni.pos.menu.model.DiscountType;

import java.math.BigDecimal;

/**
 * One discount the {@link PromotionEngine} computed for one cart line.
 * <p>
 * This is a calculation result, not a persisted row. It deliberately carries the
 * promotion code and name so the caller can snapshot them onto
 * the order service's order_item_discounts rows without re-reading the
 * promotion, which is what made the previous write path issue six queries per line.
 * <p>
 * {@link #discountValue()} is the raw magnitude the promotion asked for, a
 * percentage for {@link DiscountType#PERCENTAGE} and a monetary amount otherwise.
 * {@link #discountAmount()} is what was actually granted after the line clamp and
 * any {@link com.harmoni.pos.menu.model.PromotionRuleType#MAX_DISCOUNT_AMOUNT} cap
 * were applied, so it is always less than or equal to the line amount.
 *
 * @param orderItemId    the order line the discount was granted on
 * @param promotionId    the promotion that produced the discount
 * @param promotionCode  the promotion code, snapshotted at evaluation time
 * @param promotionName  the promotion name, snapshotted at evaluation time
 * @param discountType   how the discount was computed
 * @param discountValue  the raw magnitude the promotion asked for
 * @param discountAmount the monetary discount actually granted
 * @param targetMatch    how specifically the promotion's targets matched the line
 * @author husainahmad
 */
public record AppliedDiscount(Long orderItemId,
                              Long promotionId,
                              String promotionCode,
                              String promotionName,
                              DiscountType discountType,
                              BigDecimal discountValue,
                              BigDecimal discountAmount,
                              TargetMatch targetMatch) {

    /**
     * Canonical constructor normalising the monetary scale.
     *
     * @param orderItemId    the order line the discount was granted on
     * @param promotionId    the promotion that produced the discount
     * @param promotionCode  the promotion code
     * @param promotionName  the promotion name
     * @param discountType   how the discount was computed
     * @param discountValue  the raw magnitude the promotion asked for
     * @param discountAmount the monetary discount actually granted
     * @param targetMatch    how specifically the promotion's targets matched the line
     */
    public AppliedDiscount {
        discountValue = discountValue == null ? BigDecimal.ZERO : discountValue;
        discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
    }
}
