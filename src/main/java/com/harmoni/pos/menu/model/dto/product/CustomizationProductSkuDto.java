package com.harmoni.pos.menu.model.dto.product;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomizationProductSkuDto {

    private Integer id;

    private String name;
}
