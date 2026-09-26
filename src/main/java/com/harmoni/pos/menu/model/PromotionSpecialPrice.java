package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Entity representing a promotional price override for a single SKU.
 * <p>
 * Only meaningful for {@link PromotionType#SPECIAL_PRICE} promotions. The pair
 * {@code (promotion_id, sku_id)} is unique in the database, so re-submitting a
 * promotion replaces the previous price for the same SKU instead of duplicating it.
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionSpecialPrice {

    /**
     * The unique identifier of the special price.
     */
    private Long id;

    /**
     * The promotion this special price belongs to.
     */
    private Long promotionId;

    /**
     * The SKU whose price is overridden.
     */
    private Long skuId;

    /**
     * The promotional price to charge instead of the tier price.
     */
    private BigDecimal specialPrice;
}
