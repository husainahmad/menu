package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Sku.
 * Used for transferring SKU data between layers.
 */
@Data
public class SkuDto {
    /**
     * The name of the SKU.
     */
    @NotNull(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The product ID associated with the SKU.
     */
    private Integer productId;
}
