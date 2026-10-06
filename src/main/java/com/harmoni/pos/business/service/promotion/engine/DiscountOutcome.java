package com.harmoni.pos.business.service.promotion.engine;

import java.math.BigDecimal;
import java.util.List;

/**
 * The pricing outcome for a single cart line: what it would have cost, what the
 * promotions took off, and what the customer actually pays.
 * <p>
 * {@code discountAmount} is always the sum of {@link #discounts()} and never exceeds
 * {@link #grossAmount()}, so {@code netAmount} is never negative.
 *
 * @param orderItemId    the order line this outcome describes
 * @param grossAmount    the line amount before any promotion
 * @param discounts      the discounts granted on the line, possibly empty
 * @author husainahmad
 */
public record DiscountOutcome(Long orderItemId,
                              BigDecimal grossAmount,
                              List<AppliedDiscount> discounts) {

    /**
     * Canonical constructor defensively copying the discount list so an outcome stays
     * immutable once returned to a caller.
     *
     * @param orderItemId the order line this outcome describes
     * @param grossAmount the line amount before any promotion
     * @param discounts   the discounts granted on the line
     */
    public DiscountOutcome {
        discounts = discounts == null ? List.of() : List.copyOf(discounts);
        grossAmount = grossAmount == null ? BigDecimal.ZERO : grossAmount;
    }

    /**
     * The total discount granted on the line.
     *
     * @return the sum of every applied discount amount
     */
    public BigDecimal discountAmount() {
        return discounts.stream()
                .map(AppliedDiscount::discountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * The amount the customer pays for the line.
     *
     * @return the gross amount less the granted discounts, never negative
     */
    public BigDecimal netAmount() {
        return grossAmount.subtract(discountAmount()).max(BigDecimal.ZERO);
    }

    /**
     * Whether any promotion applied to the line.
     *
     * @return true when at least one discount was granted
     */
    public boolean isDiscounted() {
        return !discounts.isEmpty();
    }
}
