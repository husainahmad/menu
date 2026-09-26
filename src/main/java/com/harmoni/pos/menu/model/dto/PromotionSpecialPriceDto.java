package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.PromotionSpecialPrice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for a PromotionSpecialPrice.
 */
@Data
public class PromotionSpecialPriceDto {

    @NotNull(message = "{validation.promotionSpecialPrice.skuId.NotNull}")
    private Long skuId;

    @NotNull(message = "{validation.promotionSpecialPrice.specialPrice.NotNull}")
    @DecimalMin(value = "0.00", message = "{validation.promotionSpecialPrice.specialPrice.DecimalMin}")
    private BigDecimal specialPrice;

    /**
     * Converts this DTO to a PromotionSpecialPrice entity.
     *
     * @return a PromotionSpecialPrice entity, promotionId left unset
     */
    public PromotionSpecialPrice toPromotionSpecialPrice() {
        return new PromotionSpecialPrice()
                .setSkuId(skuId)
                .setSpecialPrice(specialPrice);
    }
}
