package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SkuEditDto extends SkuDto {
    @NotNull(message = "{validation.sku.id.NotBlank}")
    private Integer id;
    @JsonProperty("tierPrices")
    private @Valid List<SkuTierPriceEditDto> skuTierPriceDtos;

    public Sku toSku() {
        return new Sku()
                .setId(id)
                .setName(getName())
                .setProductId(getProductId());
    }
}
