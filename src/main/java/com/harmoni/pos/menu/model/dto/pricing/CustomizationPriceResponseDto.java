package com.harmoni.pos.menu.model.dto.pricing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The priced answer for one sale line: every chosen customization, and what they add to
 * the line.
 *
 * @author husainahmad
 */
@Data
public class CustomizationPriceResponseDto {

    /**
     * The product the answer is for, echoed back so a caller pricing several lines can
     * match answers to requests without relying on ordering.
     */
    private Integer productId;

    /**
     * The SKU the answer is for.
     */
    private Integer skuId;

    /**
     * Each chosen option, priced at the operator's tier.
     */
    private List<PricedCustomizationDto> customizations = new ArrayList<>();

    /**
     * What the chosen options add to the line in total, before any promotion. This is a
     * surcharge over the SKU price, not a discount, so it is added to
     * {@code order_detail_skus.amount}.
     */
    private BigDecimal totalAmount;
}