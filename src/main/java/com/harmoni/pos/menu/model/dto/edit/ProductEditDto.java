package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductEditDto extends ProductDto {
    @NotNull(message = "{validation.product.id.NotBlank}")
    private Integer id;
    @JsonProperty("skus")
    private @Valid List<SkuEditDto> skuDtos;

    public Product toProduct() {
        return new Product()
                .setId(id)
                .setName(getName())
                .setCategoryId(getCategoryId());
    }
}
