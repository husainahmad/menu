package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.ProductImageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductImageEditDto extends ProductImageDto {
    @NotNull(message = "{validation.product.id.NotBlank}")
    private Integer id;
}
