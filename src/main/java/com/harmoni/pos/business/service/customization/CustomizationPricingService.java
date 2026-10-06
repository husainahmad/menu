package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceResponseDto;

/**
 * Prices the customization choices on a single sale line.
 *
 * @author husainahmad
 */
public interface CustomizationPricingService {

    /**
     * Validates the choices on one order line against the catalogue and prices them.
     * <p>
     * The caller supplies only which options were chosen. Names and prices come back from
     * the catalogue, at the price tier belonging to the operator's store, because the
     * menu service is the only place that tier is known. A caller that could name its own
     * price would be able to sell an option for anything it liked.
     *
     * @param request  the product, SKU, quantity and chosen option IDs
     * @param username the operator whose store supplies the price tier
     * @return each chosen option priced and named, plus the total surcharge for the line
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if the product or SKU
     *         is missing, or the choices break a customization's required, min, max or
     *         single selection rule
     */
    CustomizationPriceResponseDto price(CustomizationPriceRequestDto request, String username);
}