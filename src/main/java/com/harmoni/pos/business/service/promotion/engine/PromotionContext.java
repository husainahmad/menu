package com.harmoni.pos.business.service.promotion.engine;

import java.time.ZoneId;

/**
 * The ambient context a cart is evaluated in: where the sale happens and which
 * clock the time-of-day rules are read against.
 * <p>
 * {@link #zone()} is explicit rather than taken from the server default because
 * {@link com.harmoni.pos.menu.model.PromotionSchedule} windows are stored as bare
 * {@link java.time.LocalTime} values. A happy hour of {@code 17:00-19:00} is
 * meaningless without knowing which store's wall clock it refers to, so the caller's
 * store supplies its own zone.
 * <p>
 * Any of the four entity references may be null when the deployment does not model
 * that level. Scope matching treats a null reference as "cannot match", so a
 * promotion scoped to a store is never applied to a basket evaluated without one.
 *
 * @param storeId  the store the sale is happening in
 * @param chainId  the chain the store belongs to
 * @param brandId  the brand the chain belongs to
 * @param tenantId the tenant owning the brand
 * @param zone     the store's local time zone, used to resolve schedules
 * @author husainahmad
 */
public record PromotionContext(Long storeId,
                               Long chainId,
                               Long brandId,
                               Long tenantId,
                               ZoneId zone) {

    /**
     * Canonical constructor defaulting the zone so a caller that does not care about
     * daylight saving transitions still gets a deterministic evaluation.
     *
     * @param storeId  the store the sale is happening in
     * @param chainId  the chain the store belongs to
     * @param brandId  the brand the chain belongs to
     * @param tenantId the tenant owning the brand
     * @param zone     the store's local time zone
     */
    public PromotionContext {
        zone = zone == null ? ZoneId.systemDefault() : zone;
    }

    /**
     * A context for a single store in the system default zone, the common case for a
     * till that only needs to know which store it is.
     *
     * @param storeId the store the sale is happening in
     * @return a context bound to the system default zone
     */
    public static PromotionContext ofStore(Long storeId) {
        return new PromotionContext(storeId, null, null, null, ZoneId.systemDefault());
    }
}
