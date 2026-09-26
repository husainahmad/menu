package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Entity representing a single condition or cap of a {@link Promotion}.
 * <p>
 * A promotion may carry several rules at once, for example a
 * {@link PromotionRuleType#MIN_QUANTITY} guard combined with a
 * {@link PromotionRuleType#MAX_DISCOUNT_AMOUNT} cap. Only the columns relevant to
 * {@link #ruleType} are populated, the rest stay {@code null}.
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionRule {

    /**
     * The unique identifier of the rule.
     */
    private Long id;

    /**
     * The promotion this rule belongs to.
     */
    private Long promotionId;

    /**
     * The condition this rule expresses, see {@link PromotionRuleType}.
     */
    private PromotionRuleType ruleType;

    /**
     * The discount magnitude: a percentage for
     * {@link PromotionRuleType#PERCENTAGE} (for example {@code 10.00} meaning ten
     * percent) or a monetary amount for {@link PromotionRuleType#FIXED_AMOUNT}.
     */
    private BigDecimal discountValue;

    /**
     * The upper bound applied to the discount computed for the whole order, used
     * by {@link PromotionRuleType#MAX_DISCOUNT_AMOUNT}.
     */
    private BigDecimal maxDiscountAmount;

    /**
     * The quantity threshold, used by {@link PromotionRuleType#MIN_QUANTITY}.
     */
    private BigDecimal minQuantity;

    /**
     * The monetary threshold, used by {@link PromotionRuleType#MIN_AMOUNT}.
     */
    private BigDecimal minAmount;
}
