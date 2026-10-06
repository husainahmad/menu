package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.PromotionScopeType;
import com.harmoni.pos.menu.model.PromotionScope;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for a PromotionScope.
 */
@Data
public class PromotionScopeDto {

    @NotNull(message = "{validation.promotionScope.scopeType.NotNull}")
    private PromotionScopeType scopeType;

    private Long scopeId;

    /**
     * Converts this DTO to a PromotionScope entity.
     *
     * @return a PromotionScope entity, promotionId left unset
     */
    public PromotionScope toPromotionScope() {
        return new PromotionScope()
                .setScopeType(scopeType)
                .setScopeId(scopeId);
    }
}