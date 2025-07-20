package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.StoreDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a Store.
 * Inherits properties from {@link StoreDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StoreEditDto extends StoreDto {
    /**
     * The ID of the store to edit.
     */
    @NotNull(message = "{validation.store.id.NotBlank}")
    private Integer id;
}
