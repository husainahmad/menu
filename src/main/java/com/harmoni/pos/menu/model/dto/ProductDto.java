package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Product;
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

    public Product toProduct() {
        return new Product()
                .setName(name)
                .setCategoryId(categoryId);
    }
}
