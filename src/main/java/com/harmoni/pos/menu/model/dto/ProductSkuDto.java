package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for Product with SKUs and their tiers.
 */
@Builder
@Data
public class ProductSkuDto {
    /**
     * The product ID.
     */
    @NotNull(message = "{validation.product.id.NotNull}")
    private Integer id;

    /**
     * The product name.
     */
    @NotBlank(message = "{validation.product.name.NotBlank}")
    private String name;

    /**
     * The category ID associated with the product.
     */
    @NotNull(message = "{validation.category.id.NotNull}")
    private Integer categoryId;

    /**
     * List of SKUs for the product, each with tier information.
     */
    @NotNull(message = "{validation.skus.NotNull}")
    private List<ProductSkuTierDto> skus;
}
