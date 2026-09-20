package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for editing a Product.
 * Inherits properties from {@link ProductDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductEditDto extends ProductDto {
    /**
     * The ID of the product to edit.
     */
    @NotNull(message = "{validation.product.id.NotBlank}")
    private Integer id;

    /**
     * List of SKUs to edit with the product.
     */
    @JsonProperty("skus")
    private @Valid List<SkuEditDto> skuDtos;

    /**
     * List of customization IDs to be assigned to the product.
     */
    @JsonProperty("customizationIds")
    private List<Integer> customizationIds;

    /**
     * Converts this DTO to a Product entity.
     *
     * @return a Product entity with updated fields
     */
    public Product toProduct() {
        return new Product()
                .setId(id)
                .setName(getName())
                .setDescription(getDescription())
                .setCategoryId(getCategoryId());
    }
}
