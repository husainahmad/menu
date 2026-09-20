package com.harmoni.pos.menu.model.dto.product;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for (re)assigning the ordered list of customizations to a product.
 * Passing an empty list clears all customization links for the product.
 */
@Data
public class ProductCustomizationReplaceDto {

    /**
     * Ordered customization IDs to assign to the product. The position in the list
     * determines the {@code sort_order} of each link.
     */
    @NotEmpty(message = "{validation.productCustomization.customizationIds.NotEmpty}")
    private List<Integer> customizationIds;
}