package com.harmoni.pos.menu.model.dto.pricing;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A sale line asking what a set of customization choices costs.
 * <p>
 * Option prices are tier scoped and the tier is resolved here, in the menu service,
 * from the operator's store. The caller therefore names only <em>which</em> options were
 * chosen, never what they cost: a client that could name a price would not need the
 * catalogue.
 *
 * @author husainahmad
 */
@Data
public class CustomizationPriceRequestDto {

    /**
     * The product the line is for. Customization groups are assigned to products, and
     * the per product overrides of required and min/max live on that link.
     */
    private Integer productId;

    /**
     * The SKU being ordered. Which options are permitted is restricted to the options
     * linked to this SKU.
     */
    private Integer skuId;

    /**
     * How many units of the SKU are being ordered. An option priced per unit is charged
     * for each one, the same way the SKU's own price is.
     */
    private Integer quantity;

    /**
     * The chosen customization option IDs. May be empty, but only when the product has no
     * required customization.
     */
    private List<Integer> customizationOptionIds = new ArrayList<>();
}