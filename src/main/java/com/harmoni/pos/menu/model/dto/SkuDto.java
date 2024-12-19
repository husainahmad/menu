package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Sku;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SkuDto {
    @NotBlank(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    private Integer productId;
    @JsonProperty("tierPrices")
    private @Valid List<SkuTierPriceDto> skuTierPriceDtos;

    public Sku toSku() {
        return new Sku()
                .setName(name)
                .setProductId(productId);
    }
}
