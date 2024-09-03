package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductSkuTierDto {
    @NotNull(message = "{validation.sku.id.NotNull}")
    private Integer id;
    @NotBlank(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.tierPrice.NotNull}")
    private ProductSkuTierPriceDto tierPrice;
}
