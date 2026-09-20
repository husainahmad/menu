package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for a customization option tier price.
 */
@Data
public class CustomizationOptionTierPriceDto {

    /**
     * The tier ID.
     */
    @NotNull(message = "{validation.tier.id.NotNull}")
    private Integer tierId;

    /**
     * The price for the tier.
     */
    @NotNull(message = "{validation.tier.price.NotNull}")
    private BigDecimal price;

    /**
     * Converts this DTO to a CustomizationOptionTierPrice entity.
     *
     * @return a CustomizationOptionTierPrice entity
     */
    public CustomizationOptionTierPrice toEntity() {
        return new CustomizationOptionTierPrice()
                .setTierId(tierId)
                .setPrice(price);
    }
}