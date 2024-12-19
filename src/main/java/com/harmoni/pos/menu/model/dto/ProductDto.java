package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductDto {

    @NotBlank(message = "{validation.product.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.product.categoryId.NotNull}")
    private Integer categoryId;
    private String description;
    @JsonProperty("skus")
    private @Valid List<SkuDto> skuDtos;
    public Product toProduct() {
        return new Product()
                .setName(name)
                .setCategoryId(categoryId);
    }
}
