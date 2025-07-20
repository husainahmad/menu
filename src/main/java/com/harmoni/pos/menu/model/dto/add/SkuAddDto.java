package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
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
public class SkuAddDto extends SkuDto {
    /**
     * List of SKUs to be added with the product.
     */

    @JsonProperty("tierPrices")
    private @Valid List<SkuTierPriceDto> skuTierPriceDtos;
    /**
     * Converts this DTO to a Product entity.
     *
     * @return a Product entity with name and categoryId set
     */

    public Sku toSku() {
        return new Sku()
                .setName(getName())
                .setProductId(getProductId());
    }
}
