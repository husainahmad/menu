package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Brand;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandDto {

    @NotBlank(message = "{validation.brand.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    public Brand toBrand() {
        return new Brand()
                .setName(name);
    }
}
