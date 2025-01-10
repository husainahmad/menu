package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.CategoryDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryEditDto extends CategoryDto {
    @NotNull(message = "{validation.category.id.NotNull}")
    private Integer id;
}
