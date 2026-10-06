package com.harmoni.pos.menu.model.dto.pricing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * One customization choice, priced and named.
 * <p>
 * The name is returned rather than left for the caller to look up, because the order
 * service stores it: a receipt printed next year must show what was ordered, not what
 * that option is called today.
 *
 * @author husainahmad
 */
@Data
public class PricedCustomizationDto {

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
     * The price of this option for one unit, at the operator's tier.
     */
    private BigDecimal price;

    /**
     * How many units of this option were chosen on the line.
     * <p>
     * The caller names an option once per unit, so a customer who wants three extra
     * shots sends the option ID three times. Two of the same option is a quantity,
     * not a second choice, and is priced as one row carrying {@code quantity = 3}.
     */
    private Integer quantity;

    /**
     * The total for the whole line: {@link #price} times {@link #quantity} times the
     * quantity of the SKU ordered.
     */
    private BigDecimal amount;
}