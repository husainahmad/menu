package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.SkuTierPrice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuTierPriceEditDto {
    @NotNull(message = "{validation.sku.id.NotNull}")
    private Integer skuId;
    @NotNull(message = "{validation.skutierprice.tierId.NotNull}")
    private Integer tierId;
    @NotNull(message = "{validation.price.tierId.NotNull}")
    private BigDecimal price;

    public SkuTierPrice toSkuTierPrice() {
        return new SkuTierPrice()
                .setSkuId(skuId)
                .setTierId(tierId)
                .setPrice(price);
    }
}