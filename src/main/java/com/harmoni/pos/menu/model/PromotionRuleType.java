package com.harmoni.pos.menu.model;

/**
 * Enumeration of the individual conditions that make up a {@link PromotionRule}.
 * <p>
 * Persisted as {@code promotion_rules.rule_type}. Each constant maps to a specific
 * subset of the rule columns, so a single rule row carries only the values that
 * are relevant to its type: {@code discount_value} for {@link #PERCENTAGE} and
 * {@link #FIXED_AMOUNT}, {@code max_discount_amount} for {@link #MAX_DISCOUNT_AMOUNT},
 * {@code min_quantity} for {@link #MIN_QUANTITY} and {@code min_amount} for
 * {@link #MIN_AMOUNT}.
 */
public enum PromotionRuleType {

    /** Reduce the price by a percentage; requires {@code discount_value}. */
    PERCENTAGE,

    /** Reduce the price by a monetary amount; requires {@code discount_value}. */
    FIXED_AMOUNT,

    /** Cap the total discount; requires {@code max_discount_amount}. */
    MAX_DISCOUNT_AMOUNT,

    /** Only apply when the quantity reaches a threshold; requires {@code min_quantity}. */
    MIN_QUANTITY,

    /** Only apply when the line amount reaches a threshold; requires {@code min_amount}. */
    MIN_AMOUNT
}
