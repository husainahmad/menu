package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.Store;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SkuTierPriceDto {

    @NotNull(message = "{validation.skutierprice.skuId.NotNull}")
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