package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Product.
 * Used for transferring product data between layers.
 */
@Data
public class ProductDto {

    /**
     * The name of the product.
     */
    @NotBlank(message = "{validation.product.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The category ID associated with the product.
     */
    @NotNull(message = "{validation.product.categoryId.NotNull}")
    private Integer categoryId;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The product image edit data transfer object.
     */
    @JsonProperty("productImage")
    private ProductImageEditDto productImageEditDto;
}
