package com.harmoni.pos.menu.model.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class CustomizationProductDto {

    private Integer id;

    private String name;

    private Integer categoryId;

    private List<CustomizationProductSkuDto> skus = new ArrayList<>();

    @JsonIgnore
    private Integer brandId;
}
