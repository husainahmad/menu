package com.harmoni.pos.menu.model.dto.product;

import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.SelectionType;
import lombok.Data;

import java.util.List;

/**
 * Response DTO describing one customization assigned to a product.
 * The selection fields carry the <em>effective</em> values (override if present,
 * otherwise the customization master value), so consumers do not need to resolve them.
 */
@Data
public class ProductCustomizationResponseDto {

    /**
     * The product_customizations link id.
     */
    private Integer id;

    /**
     * The product this customization is assigned to.
     */
    private Integer productId;

    /**
     * The customization (master) id.
     */
    private Integer customizationId;

    /**
     * The customization name.
     */
    private String name;

    /**
     * The customization description.
     */
    private String description;

    /**
     * The customization selection type (single or multiple).
     */
    private SelectionType selectionType;

    /**
     * The brand the customization belongs to.
     */
    private Integer brandId;

    /**
     * Effective "required" flag for this product.
     */
    private Boolean required;

    /**
     * Effective minimum selection for this product.
     */
    private Integer minSelection;

    /**
     * Effective maximum selection for this product.
     */
    private Integer maxSelection;

    /**
     * Per-product override of the required flag (null = no override).
     */
    private Boolean requiredOverride;

    /**
     * Per-product override of the minimum selection (null = no override).
     */
    private Integer minSelectionOverride;

    /**
     * Per-product override of the maximum selection (null = no override).
     */
    private Integer maxSelectionOverride;

    /**
     * Position of the customization relative to the others assigned to the product.
     */
    private Integer sortOrder;

    /**
     * The customization options, each with their tier prices attached.
     */
    private List<CustomizationOption> options;
}