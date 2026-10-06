package com.harmoni.pos.business.service.promotion.engine;

import java.util.List;

/**
 * Prices a cart against the live promotion catalogue.
 * <p>
 * The engine is the authority on what a promotion is worth. It is a pure calculator:
 * it reads the catalogue, computes discounts and returns them, but writes nothing.
 * Persisting the result is the caller's job, which keeps the pricing rules testable
 * without a database and keeps the write path free to batch.
 * <p>
 * Nothing a client submits is trusted. A caller asking for a discount on promotion
 * {@code 42} gets what promotion {@code 42} actually grants under the current status,
 * date range, schedule window, scope and rules, or nothing at all.
 *
 * @author husainahmad
 */
public interface PromotionEngine {

    /**
     * Prices every line of a cart.
     *
     * @param lines   the cart lines, in the order they should be reported
     * @param context the store, chain and zone the sale is happening in
     * @return one outcome per input line, in the same order, with the discounts granted
     */
    List<DiscountOutcome> evaluate(List<CartLine> lines, PromotionContext context);

    /**
     * Prices every line of a cart under the default {@link StackingPolicy#EXCLUSIVE}
     * policy, so only one promotion can apply to any given line.
     *
     * @param lines   the cart lines, in the order they should be reported
     * @param context the store, chain and zone the sale is happening in
     * @return one outcome per input line, in the same order, with the discounts granted
     */
    List<DiscountOutcome> evaluateExclusive(List<CartLine> lines, PromotionContext context);

    /**
     * Prices every line of a cart, summing every eligible promotion on a line.
     *
     * @param lines   the cart lines, in the order they should be reported
     * @param context the store, chain and zone the sale is happening in
     * @return one outcome per input line, in the same order, with the discounts granted
     */
    List<DiscountOutcome> evaluateStacked(List<CartLine> lines, PromotionContext context);

    /**
     * Prices a cart and returns only the discounts, flattened across lines, ready to
     * be snapshotted onto the order service's order_item_discounts rows.
     * <p>
     * The promotion code and name travel with each discount so the caller never has to
     * re-read the promotion to make the audit row self-describing.
     *
     * @param lines   the cart lines, in the order they should be reported
     * @param context the store, chain and zone the sale is happening in
     * @return every discount granted across the cart, possibly empty
     */
    List<AppliedDiscount> computeDiscounts(List<CartLine> lines, PromotionContext context);
}
