package com.harmoni.pos.business.service.promotion.pricing;

import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceResponseDto;

/**
 * Prices a basket for a caller that cannot reach the promotion catalogue itself, such
 * as the order service at checkout.
 * <p>
 * This sits in front of
 * {@link com.harmoni.pos.business.service.promotion.engine.PromotionEngine} purely to
 * translate the wire format into engine types and back. The pricing decisions all
 * belong to the engine: this service never inspects a promotion, never applies a
 * percentage and never adjusts a total of its own accord.
 *
 * @author husainahmad
 */
public interface PromotionPricingService {

    /**
     * Prices every requested line against the live promotion catalogue.
     *
     * @param request the basket and the sale context
     * @return one priced line per requested line, in request order
     */
    PromotionPriceResponseDto price(PromotionPriceRequestDto request);
}
