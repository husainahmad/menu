package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.CategoryDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a Category.
 * Inherits properties from {@link CategoryDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryEditDto extends CategoryDto {
    /**
     * The ID of the category to edit.
     */
    @NotNull(message = "{validation.category.id.NotNull}")
    private Integer id;
}
