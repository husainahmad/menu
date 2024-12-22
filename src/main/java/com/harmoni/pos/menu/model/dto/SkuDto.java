package com.harmoni.pos.menu.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkuDto {
    @NotNull(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    private Integer productId;
}
