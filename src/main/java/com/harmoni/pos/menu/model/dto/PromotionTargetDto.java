package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.PromotionTarget;
import com.harmoni.pos.menu.model.PromotionTargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for a PromotionTarget.
 */
@Data
public class PromotionTargetDto {

    @NotNull(message = "{validation.promotionTarget.targetType.NotNull}")
    private PromotionTargetType targetType;

    private Long productId;

    private Long skuId;

    private Long categoryId;

    /**
     * Converts this DTO to a PromotionTarget entity.
     *
     * @return a PromotionTarget entity, promotionId left unset
     */
    public PromotionTarget toPromotionTarget() {
        return new PromotionTarget()
                .setTargetType(targetType)
                .setProductId(productId)
                .setSkuId(skuId)
                .setCategoryId(categoryId);
    }
}
