package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDto {

    @NotBlank(message = "{validation.category.name.NotBlank}")
    private String name;
    @NotBlank(message = "{validation.category.description.NotBlank}")
    private String description;
    @NotNull(message = "{validation.category.brandId.NotNull}")
    private Integer brandId;

    public Category toCategory() {
        return new Category()
                .setName(name)
                .setDescription(description)
                .setBrandId(brandId);
    }
}
