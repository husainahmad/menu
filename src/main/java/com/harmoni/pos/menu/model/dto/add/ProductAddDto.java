package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import com.harmoni.pos.menu.model.dto.SkuDto;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductAddDto extends ProductDto {

    @JsonProperty("skus")
    private @Valid List<SkuDto> skuDtos;

    public Product toProduct() {
        return new Product()
                .setName(getName())
                .setCategoryId(getCategoryId());
    }
}
