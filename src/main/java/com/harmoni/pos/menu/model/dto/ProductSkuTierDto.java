package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object for Product SKU with tier price information.
 */
@Builder
@Data
public class ProductSkuTierDto {
    /**
     * The SKU ID.
     */
    @NotNull(message = "{validation.sku.id.NotNull}")
    private Integer id;

    /**
     * The SKU name.
     */
    @NotBlank(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The tier price information for this SKU.
     */
    @NotNull(message = "{validation.tierPrice.NotNull}")
    private ProductSkuTierPriceDto tierPrice;
}
