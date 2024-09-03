package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductSkuDto {
    @NotNull(message = "{validation.product.id.NotNull}")
    private Integer id;
    @NotBlank(message = "{validation.product.name.NotBlank}")
    private String name;
    @NotNull(message = "{validation.category.id.NotNull}")
    private Integer categoryId;
    @NotNull(message = "{validation.skus.NotNull}")
    private List<ProductSkuTierDto> skus;
}
