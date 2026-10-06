package com.harmoni.pos.business.service.promotion.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A single cart line handed to the {@link PromotionEngine} for evaluation.
 * <p>
 * Deliberately decoupled from {@code OrderDetail}: the engine is a pure calculator
 * and must not know how a basket is persisted. The caller is responsible for
 * resolving {@link #categoryId}, which the catalog stores on the product and the SKU
 * rather than on the order line.
 * <p>
 * {@link #quantity} is a {@link BigDecimal} even though {@code OrderDetail} carries a
 * {@code Double}. Fractional quantities are legal in food and beverage POS, and the
 * discount arithmetic must not pass through binary floating point.
 *
 * @param orderItemId the persisted order line ID, used to key the resulting discounts
 * @param productId   the product the line was ordered from
 * @param skuId       the SKU variant ordered, may be null when the product has none
 * @param categoryId  the category the product belongs to, used by category targets
 * @param unitPrice   the pre-discount price charged for one unit
 * @param quantity    how many units the line carries
 * @author husainahmad
 */
public record CartLine(Long orderItemId,
                       Long productId,
                       Long skuId,
                       Long categoryId,
                       BigDecimal unitPrice,
                       BigDecimal quantity) {

    private static final int MONEY_SCALE = 2;

    /**
     * Canonical constructor normalising the money and quantity scales so the engine
     * never mixes precisions part way through a calculation.
     *
     * @param orderItemId the persisted order line ID
     * @param productId   the product the line was ordered from
     * @param skuId       the SKU variant ordered
     * @param categoryId  the category the product belongs to
     * @param unitPrice   the pre-discount price charged for one unit
     * @param quantity    how many units the line carries
     */
    public CartLine {
        unitPrice = scale(unitPrice);
        quantity = quantity == null ? BigDecimal.ZERO : quantity.stripTrailingZeros();
    }

    /**
     * The gross line amount before any promotion is applied, that is
     * {@code unitPrice * quantity} rounded to the money scale.
     *
     * @return the gross line amount
     */
    public BigDecimal grossAmount() {
        return unitPrice.multiply(quantity).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Whether the line can carry a discount at all. A line with a non-positive price
     * or quantity has no monetary value to discount, and treating it as eligible
     * would let a promotion drive a line negative.
     *
     * @return true when the line has a positive gross amount
     */
    public boolean isDiscountable() {
        return unitPrice.signum() > 0 && quantity.signum() > 0;
    }

    private static BigDecimal scale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
