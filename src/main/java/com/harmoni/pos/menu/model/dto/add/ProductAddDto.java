package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for adding a new Product.
 * Inherits properties from {@link ProductDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductAddDto extends ProductDto {

    /**
     * List of SKUs to be added with the product.
     */
    @JsonProperty("skus")
    private @Valid List<SkuAddDto> skuDtos;

    /**
     * List of customization IDs to be assigned to the product.
     */
    @JsonProperty("customizationIds")
    private List<Integer> customizationIds;

    /**
     * Converts this DTO to a Product entity.
     *
     * @return a Product entity with name and categoryId set
     */
    public Product toProduct() {
        return new Product()
                .setName(getName())
                .setCategoryId(getCategoryId());
    }
}
