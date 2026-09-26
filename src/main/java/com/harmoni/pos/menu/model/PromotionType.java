package com.harmoni.pos.menu.model;

/**
 * Enumeration of the promotion mechanisms supported by the POS.
 * <p>
 * The value is persisted as {@code promotions.promotion_type} and decides which
 * child collection carries the actual pricing logic: a percentage or fixed amount
 * promotion is driven by {@link PromotionRule} rows, whereas a
 * {@link #SPECIAL_PRICE} promotion is driven by {@link PromotionSpecialPrice} rows.
 */
public enum PromotionType {

    /** Price reduced by a percentage taken from a {@link PromotionRule}. */
    PERCENTAGE,

    /** Price reduced by a fixed monetary amount taken from a {@link PromotionRule}. */
    FIXED_AMOUNT,

    /** Price overridden per SKU through {@link PromotionSpecialPrice}. */
    SPECIAL_PRICE,

    /** Buy a minimum quantity and get extra quantity for free. */
    BUY_X_GET_Y,

    /** Fixed bundle price combining several targets. */
    BUNDLE
}
