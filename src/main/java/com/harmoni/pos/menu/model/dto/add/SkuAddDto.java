package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SkuAddDto extends SkuDto {

    @JsonProperty("tierPrices")
    private @Valid List<SkuTierPriceDto> skuTierPriceDtos;

    public Sku toSku() {
        return new Sku()
                .setName(getName())
                .setProductId(getProductId());
    }
}
