package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.dto.PromotionDto;
import com.harmoni.pos.menu.model.dto.PromotionRuleDto;
import com.harmoni.pos.menu.model.dto.PromotionScheduleDto;
import com.harmoni.pos.menu.model.dto.PromotionSpecialPriceDto;
import com.harmoni.pos.menu.model.dto.PromotionTargetDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for editing a Promotion.
 * <p>
 * Each child collection follows the usual partial-update convention: omitting it
 * leaves the stored rows untouched, while submitting an empty list clears it. The
 * whole update runs in a single transaction.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PromotionEditDto extends PromotionDto {

    /**
     * The ID of the promotion to edit.
     */
    @NotNull(message = "{validation.promotion.id.NotNull}")
    private Long id;

    /**
     * The redeemable time windows replacing the current ones.
     */
    @JsonProperty("schedules")
    private @Valid List<PromotionScheduleDto> scheduleDtos;

    /**
     * The catalog targets replacing the current ones.
     */
    @JsonProperty("targets")
    private @Valid List<PromotionTargetDto> targetDtos;

    /**
     * The rules replacing the current ones.
     */
    @JsonProperty("rules")
    private @Valid List<PromotionRuleDto> ruleDtos;

    /**
     * The per-SKU price overrides replacing the current ones.
     */
    @JsonProperty("specialPrices")
    private @Valid List<PromotionSpecialPriceDto> specialPriceDtos;

    /**
     * Converts this DTO to a Promotion entity, without the child collections.
     *
     * @return a Promotion entity carrying the id and the updated scalar attributes
     */
    public Promotion toPromotion() {
        return super.toPromotion()
                .setId(id);
    }
}
