package com.harmoni.pos.business.service.validation;

import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsRequest;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsResponseDto;

/**
 * Confirms the lines of an order against the menu, and hands back the official menu data
 * for them.
 * <p>
 * This is the boundary the order service checks its basket against before creating an
 * order. Everything it is told about a line is an ID or a quantity; everything it is told
 * in return about the catalogue comes from here. That is the whole point of the exchange:
 * a basket assembled over days, on a cached client, against a menu that has since changed
 * cannot be assumed to still describe what the shop sells.
 * <p>
 * Nothing is persisted, and no total is produced. Totalling a line is arithmetic the
 * order service does, because it holds the quantities and the promotions; this service
 * only knows what things are called and what they cost.
 *
 * @author husainahmad
 */
public interface OrderItemValidationService {

    /**
     * Confirms every line of the basket against the catalogue.
     * <p>
     * Either the whole basket is confirmed or the first line that cannot be sold is
     * refused, naming why. Partial confirmation is not offered: an order built from some
     * of the lines the customer chose is not the order the customer chose.
     *
     * @param request the store, and the lines to confirm
     * @return the confirmed lines with the catalogue data attached
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if a line names
     *         something that does not exist, is not available at the store, is not a legal
     *         choice for its product or SKU, or carries an illegal quantity
     */
    ValidateOrderItemsResponseDto validate(ValidateOrderItemsRequest request);
}