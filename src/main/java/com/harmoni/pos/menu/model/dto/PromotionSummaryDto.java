package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PromotionSummaryDto {

    private Long id;

    private String code;

    private String name;

    private PromotionType promotionType;

    private PromotionStatus status;

    @JsonIgnore
    private Integer priority;
}
