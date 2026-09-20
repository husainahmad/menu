package com.harmoni.pos.menu.model.dto.product;

import lombok.Data;

/**
 * Request DTO for updating the per-product override configuration of a
 * product-customization link. Null values mean "no override", so the effective value
 * falls back to the customization master.
 */
@Data
public class ProductCustomizationConfigDto {

    /**
     * Per-product override of the "required" flag. Null keeps the master value.
     */
    private Boolean requiredOverride;

    /**
     * Per-product override of the minimum selection. Null keeps the master value.
     */
    private Integer minSelectionOverride;

    /**
     * Per-product override of the maximum selection. Null keeps the master value.
     */
    private Integer maxSelectionOverride;

    /**
     * Position of this customization relative to the others assigned to the product.
     */
    private Integer sortOrder;
}