package com.harmoni.pos.menu.model.dto.pricing;

import com.harmoni.pos.business.service.promotion.engine.PromotionContext;
import lombok.Data;

import java.time.ZoneId;

/**
 * The sale context a price is requested in: which store, and in which time zone.
 * <p>
 * The zone matters as much as the store. A promotion scheduled for Tuesday 09:00 to
 * 11:00 must be evaluated in the store's own wall clock, otherwise a store in Sydney
 * prices a Monday-evening order against Tuesday's schedule.
 * <p>
 * The chain, brand and tenant identifiers are optional. When omitted, promotions scoped
 * to them cannot match, which is the safe default: an unscoped call must never receive
 * a promotion reserved for a specific chain.
 *
 * @author husainahmad
 */
@Data
public class PromotionContextDto {

    private Long storeId;

    private Long chainId;

    private Long brandId;

    private Long tenantId;

    /**
     * The store's IANA time zone, for example {@code Australia/Sydney}.
     */
    private String zone;

    /**
     * Converts this DTO to the engine's context, falling back to the system default zone
     * when the caller did not supply one.
     *
     * @return the engine promotion context
     */
    public PromotionContext toPromotionContext() {
        return new PromotionContext(storeId, chainId, brandId, tenantId, toZone());
    }

    private ZoneId toZone() {
        if (zone == null || zone.isBlank()) {
            return ZoneId.systemDefault();
        }
        return ZoneId.of(zone);
    }
}
