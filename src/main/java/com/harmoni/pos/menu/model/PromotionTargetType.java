package com.harmoni.pos.menu.model;

/**
 * Enumeration of the catalog levels a {@link PromotionTarget} can point at.
 * <p>
 * The value is persisted as {@code promotion_targets.target_type} and determines
 * which of the three nullable foreign key columns must be populated. Exactly one
 * of {@code product_id}, {@code sku_id} or {@code category_id} is expected to be
 * set for a given target row.
 */
public enum PromotionTargetType {

    /** Applies to a single product; requires {@code product_id}. */
    PRODUCT,

    /** Applies to a single SKU (size or variant); requires {@code sku_id}. */
    SKU,

    /** Applies to every product in a category; requires {@code category_id}. */
    CATEGORY
}
