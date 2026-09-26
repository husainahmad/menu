package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.PromotionRule;
import com.harmoni.pos.menu.model.PromotionRuleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for a PromotionRule.
 */
@Data
public class PromotionRuleDto {

    @NotNull(message = "{validation.promotionRule.ruleType.NotNull}")
    private PromotionRuleType ruleType;

    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minQuantity;

    private BigDecimal minAmount;

    /**
     * Converts this DTO to a PromotionRule entity.
     *
     * @return a PromotionRule entity, promotionId left unset
     */
    public PromotionRule toPromotionRule() {
        return new PromotionRule()
                .setRuleType(ruleType)
                .setDiscountValue(discountValue)
                .setMaxDiscountAmount(maxDiscountAmount)
                .setMinQuantity(minQuantity)
                .setMinAmount(minAmount);
    }
}
