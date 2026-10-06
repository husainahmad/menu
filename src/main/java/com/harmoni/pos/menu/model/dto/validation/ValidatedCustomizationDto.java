package com.harmoni.pos.menu.model.dto.validation;

import lombok.Data;

import java.math.BigDecimal;

/**
 * One customization choice, named and priced as the catalogue currently has it.
 * <p>
 * No total is carried. Multiplying this price by the quantities is the order service's
 * arithmetic to do: it knows the line quantity and any promotions that apply, and this
 * service is not in a position to total a sale.
 *
 * @author husainahmad
 */
@Data
public class ValidatedCustomizationDto {

    /**
     * The chosen option.
     */
    private Integer customizationOptionId;

    /**
     * The group the option belongs to, so the caller can tell "extra cheese" from the
     * milk choice it was made under.
     */
    private Integer customizationId;

    /**
     * The group name as it is currently configured.
     */
    private String customizationName;

    /**
     * The option name as it is currently configured.
     */
    private String optionName;

    /**
     * The price of this option for one unit, at the store's tier.
     */
    private BigDecimal price;

    /**
     * The validated number of selections of this option on the line, echoed back so the
     * caller can store what was actually accepted.
     */
    private Integer quantity;
}