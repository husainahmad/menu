package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.ProductImageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a User.
 * Inherits properties from {@link UserDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductImageEditDto extends ProductImageDto {
    /**
     * The ID of the user to edit.
     */
    @NotNull(message = "{validation.product.id.NotBlank}")
    private Integer id;
}
