package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Table.
 * Used for transferring table data between layers.
 */
@Data
public class TableDto {

    /**
     * The store ID associated with the table.
     */
    @NotNull(message = "{validation.table.storeId.NotNull}")
    private Integer storeId;

    /**
     * The name of the table.
     */
    @NotBlank(message = "{validation.table.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The seating capacity of the table.
     */
    @NotNull(message = "{validation.table.capacity.NotNull}")
    @Min(value = 1, message = "{validation.table.capacity.Min}")
    private Integer capacity;

    /**
     * Converts this DTO to a Table entity.
     *
     * @return a Table entity with storeId, name and capacity set
     */
    public Table toTable() {
        return new Table()
                .setStoreId(storeId)
                .setName(name)
                .setCapacity(capacity);
    }
}