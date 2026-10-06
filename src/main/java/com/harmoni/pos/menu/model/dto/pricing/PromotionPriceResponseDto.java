package com.harmoni.pos.menu.model.dto.pricing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * The priced basket: one entry per requested line, plus the order wide saving.
 * <p>
 * {@code totalDiscount} is included so the caller does not have to sum the lines
 * itself, and is guaranteed to be the exact sum of the per line discounts.
 *
 * @author husainahmad
 */
@Data
public class PromotionPriceResponseDto {

    private List<PricedLineDto> lines;

    /**
     * The sum of every line's {@link PricedLineDto#getDiscountAmount()}.
     */
    private BigDecimal totalDiscount;

    /**
     * The sum of every line's {@link PricedLineDto#getNetAmount()}.
     */
    private BigDecimal totalNet;
}
