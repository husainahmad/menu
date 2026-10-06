package com.harmoni.pos.menu.model.dto.pricing;

import com.harmoni.pos.business.service.promotion.engine.AppliedDiscount;
import com.harmoni.pos.business.service.promotion.engine.TargetMatch;
import com.harmoni.pos.menu.model.DiscountType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * One discount the engine granted, flattened for transport.
 * <p>
 * The promotion code and name travel with the discount so the caller can snapshot an
 * audit row without a second lookup, and so a historic sale stays readable after the
 * promotion is later renamed or deleted.
 *
 * @author husainahmad
 */
@Data
public class AppliedDiscountDto {

    private Long promotionId;

    private String promotionCode;

    private String promotionName;

    private DiscountType discountType;

    /**
     * The magnitude before it was resolved against the line: a percentage for
     * {@link DiscountType#PERCENTAGE}, otherwise the monetary value.
     */
    private BigDecimal discountValue;

    /**
     * What the discount actually took off this line.
     */
    private BigDecimal discountAmount;

    /**
     * How specifically the promotion's targets matched the line.
     */
    private TargetMatch targetMatch;

    /**
     * Converts an engine discount to its transport form.
     *
     * @param applied the engine discount
     * @return the flattened discount
     */
    public static AppliedDiscountDto from(AppliedDiscount applied) {
        AppliedDiscountDto dto = new AppliedDiscountDto();
        dto.setPromotionId(applied.promotionId());
        dto.setPromotionCode(applied.promotionCode());
        dto.setPromotionName(applied.promotionName());
        dto.setDiscountType(applied.discountType());
        dto.setDiscountValue(applied.discountValue());
        dto.setDiscountAmount(applied.discountAmount());
        dto.setTargetMatch(applied.targetMatch());
        return dto;
    }
}
