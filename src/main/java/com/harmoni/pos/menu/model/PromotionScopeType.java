package com.harmoni.pos.menu.model;

/**
 * Enumeration of the scope levels a {@link PromotionScope} can point at.
 * <p>
 * The value is persisted as {@code promotion_scope.scope_type} and determines
 * which of the four nullable references is authoritative. Exactly one of
 * {@code tenant_id}, {@code brand_id}, {@code chain_id} or {@code store_id}
 * is expected to be set for a given scope row.
 */
public enum PromotionScopeType {

    /** Applies to a tenant; requires {@code tenant_id}. */
    TENANT,

    /** Applies to a brand; requires {@code brand_id}. */
    BRAND,

    /** Applies to a chain; requires {@code chain_id}. */
    CHAIN,

    /** Applies to a store; requires {@code store_id}. */
    STORE
}