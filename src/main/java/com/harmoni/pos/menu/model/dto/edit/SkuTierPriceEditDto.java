package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.SkuTierPrice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for editing Sku Tier Price.
 */
@Data
public class SkuTierPriceEditDto {
    /**
     * The SKU ID for the tier price.
     */
    @NotNull(message = "{validation.sku.id.NotNull}")
    private Integer skuId;

    /**
     * The Tier ID for the tier price.
     */
    @NotNull(message = "{validation.skutierprice.tierId.NotNull}")
    private Integer tierId;

    /**
     * The price for the SKU and Tier.
     */
    @NotNull(message = "{validation.price.tierId.NotNull}")
    private BigDecimal price;

    /**
     * Converts this DTO to a SkuTierPrice entity.
     *
     * @return a SkuTierPrice entity with updated fields
     */
    public SkuTierPrice toSkuTierPrice() {
        return new SkuTierPrice()
                .setSkuId(skuId)
                .setTierId(tierId)
                .setPrice(price);
    }
}