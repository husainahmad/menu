package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data transfer object carrying the scalar attributes of a Promotion.
 */
@Data
public class PromotionDto {

    @NotBlank(message = "{validation.promotion.code.NotBlank}")
    @Size(max = 50, message = "{validation.promotion.code.Size}")
    private String code;

    @NotBlank(message = "{validation.promotion.name.NotBlank}")
    @Size(max = 100, message = "{validation.promotion.name.Size}")
    private String name;

    @Size(max = 255, message = "{validation.promotion.description.Size}")
    private String description;

    @NotNull(message = "{validation.promotion.promotionType.NotNull}")
    private PromotionType promotionType;

    @NotNull(message = "{validation.promotion.status.NotNull}")
    private PromotionStatus status;

    @PositiveOrZero(message = "{validation.promotion.priority.PositiveOrZero}")
    private Integer priority;

    private LocalDate startDate;

    private LocalDate endDate;

    /**
     * Converts this DTO to a Promotion entity, without the child collections.
     *
     * @return a Promotion entity carrying the scalar attributes
     */
    public Promotion toPromotion() {
        return new Promotion()
                .setCode(code)
                .setName(name)
                .setDescription(description)
                .setPromotionType(promotionType)
                .setStatus(status)
                .setPriority(priority == null ? 0 : priority)
                .setStartDate(startDate)
                .setEndDate(endDate);
    }
}
