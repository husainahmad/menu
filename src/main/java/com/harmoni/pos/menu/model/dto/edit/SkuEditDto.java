package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for editing a Sku.
 * Inherits properties from {@link SkuDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SkuEditDto extends SkuDto {
    /**
     * The ID of the SKU to edit.
     */
    @NotNull(message = "{validation.sku.id.NotBlank}")
    private Integer id;

    /**
     * List of tier prices to edit for this SKU.
     */
    @JsonProperty("tierPrices")
    private @Valid List<SkuTierPriceEditDto> skuTierPriceDtos;

    /**
     * Converts this DTO to a Sku entity.
     *
     * @return a Sku entity with updated fields
     */
    public Sku toSku() {
        return new Sku()
                .setId(id)
                .setName(getName())
                .setProductId(getProductId());
    }
}
