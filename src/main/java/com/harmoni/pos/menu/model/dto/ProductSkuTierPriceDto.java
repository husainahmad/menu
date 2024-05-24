package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ProductSkuTierPriceDto {
    @NotNull(message = "{validation.tier.id.NotNull}")
    private Integer id;
    @NotNull(message = "{validation.tier.price.NotNull}")
    private BigDecimal price;
}
