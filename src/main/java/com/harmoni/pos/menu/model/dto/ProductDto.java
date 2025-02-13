package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDto {

    @NotBlank(message = "{validation.product.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.product.categoryId.NotNull}")
    private Integer categoryId;
    private String description;
    @JsonProperty("productImage")
    private ProductImageEditDto productImageEditDto;
}
