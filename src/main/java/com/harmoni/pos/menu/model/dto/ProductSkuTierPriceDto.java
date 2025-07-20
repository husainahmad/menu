package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for SKU tier price.
 */
@Builder
@Data
public class ProductSkuTierPriceDto {
    /**
     * The tier ID.
     */
    @NotNull(message = "{validation.tier.id.NotNull}")
    private Integer id;

    /**
     * The price for the tier.
     */
    @NotNull(message = "{validation.tier.price.NotNull}")
    private BigDecimal price;
}
