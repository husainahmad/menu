package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for Category.
 * Used for transferring category data between layers.
 */
@Data
public class CategoryDto {

    /**
     * The name of the category.
     */
    @NotBlank(message = "{validation.category.name.NotBlank}")
    private String name;

    /**
     * The description of the category.
     */
    @NotBlank(message = "{validation.category.description.NotBlank}")
    private String description;

    /**
     * The brand ID associated with the category.
     */
    @NotNull(message = "{validation.category.brandId.NotNull}")
    private Integer brandId;

    /**
     * Indicates if the category is active.
     */
    private Boolean active;

    /**
     * Converts this DTO to a Category entity.
     *
     * @return a Category entity with name, description, and brandId set
     */
    public Category toCategory() {
        return new Category()
                .setName(name)
                .setDescription(description)
                .setBrandId(brandId);
    }
}
