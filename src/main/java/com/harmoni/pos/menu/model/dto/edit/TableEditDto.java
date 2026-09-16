package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.TableDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a Table.
 * Inherits properties from {@link TableDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableEditDto extends TableDto {
    /**
     * The ID of the table to edit.
     */
    @NotNull(message = "{validation.table.id.NotNull}")
    private Integer id;
}