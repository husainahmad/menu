package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.dto.PromotionDto;
import com.harmoni.pos.menu.model.dto.PromotionRuleDto;
import com.harmoni.pos.menu.model.dto.PromotionScheduleDto;
import com.harmoni.pos.menu.model.dto.PromotionSpecialPriceDto;
import com.harmoni.pos.menu.model.dto.PromotionTargetDto;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for adding a new Promotion together with its whole
 * aggregate. Inherits the scalar attributes from {@link PromotionDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PromotionAddDto extends PromotionDto {

    /**
     * The redeemable time windows to create with the promotion.
     */
    @JsonProperty("schedules")
    private @Valid List<PromotionScheduleDto> scheduleDtos;

    /**
     * The catalog targets to create with the promotion.
     */
    @JsonProperty("targets")
    private @Valid List<PromotionTargetDto> targetDtos;

    /**
     * The rules to create with the promotion.
     */
    @JsonProperty("rules")
    private @Valid List<PromotionRuleDto> ruleDtos;

    /**
     * The per-SKU price overrides to create with the promotion. Only meaningful
     * when the promotion type is SPECIAL_PRICE.
     */
    @JsonProperty("specialPrices")
    private @Valid List<PromotionSpecialPriceDto> specialPriceDtos;

    /**
     * Converts this DTO to a Promotion entity, without the child collections.
     *
     * @return a Promotion entity carrying the scalar attributes
     */
    public Promotion toPromotion() {
        return super.toPromotion();
    }
}
