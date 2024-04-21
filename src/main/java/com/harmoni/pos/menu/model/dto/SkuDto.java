package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Sku;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkuDto {
    @NotBlank(message = "{validation.sku.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.sku.productId.NotNull}")
    private Integer productId;

    public Sku toSku() {
        return new Sku()
                .setName(name)
                .setProductId(productId);
    }
}
