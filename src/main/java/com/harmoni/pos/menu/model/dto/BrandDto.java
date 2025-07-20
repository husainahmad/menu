package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Brand;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Brand.
 * Used for transferring brand data between layers.
 */
@Data
public class BrandDto {

    /**
     * The name of the brand.
     */
    @NotBlank(message = "{validation.brand.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * Converts this DTO to a Brand entity.
     *
     * @return a Brand entity with the name set
     */
    public Brand toBrand() {
        return new Brand()
                .setName(name);
    }
}
