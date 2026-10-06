package com.harmoni.pos.menu.model.dto.pricing;

import com.harmoni.pos.business.service.promotion.engine.DiscountOutcome;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * What one line of a basket costs once promotions are applied.
 * <p>
 * {@code discountAmount} is always the sum of {@link #discounts} and never exceeds
 * {@code grossAmount}, so {@code netAmount} is never negative. The gross amount is
 * returned alongside the net so the caller can show a customer both figures without
 * having to re-derive the line total.
 *
 * @author husainahmad
 */
@Data
public class PricedLineDto {

    /**
     * Echoes back the {@code lineIndex} the caller assigned to this line.
     */
    private Integer lineIndex;

    /**
     * The line amount before any promotion, that is unit price times quantity.
     */
    private BigDecimal grossAmount;

    /**
     * The total the promotions took off this line.
     */
    private BigDecimal discountAmount;

    /**
     * What the customer pays for this line.
     */
    private BigDecimal netAmount;

    /**
     * Whether any promotion applied to the line.
     */
    private boolean discounted;

    /**
     * The individual discounts granted, so the caller can record which promotion
     * produced which part of the saving.
     */
    private List<AppliedDiscountDto> discounts;

    /**
     * Converts an engine outcome to its transport form.
     *
     * @param lineIndex the index the caller assigned to the line
     * @param outcome   the engine outcome
     * @return the priced line
     */
    public static PricedLineDto of(int lineIndex, DiscountOutcome outcome) {
        PricedLineDto dto = new PricedLineDto();
        dto.setLineIndex(lineIndex);
        dto.setGrossAmount(outcome.grossAmount());
        dto.setDiscountAmount(outcome.discountAmount());
        dto.setNetAmount(outcome.netAmount());
        dto.setDiscounted(outcome.isDiscounted());
        dto.setDiscounts(outcome.discounts().stream().map(AppliedDiscountDto::from).toList());
        return dto;
    }
}
