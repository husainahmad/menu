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
     * The floor the table stands on.
     */
    @NotNull(message = "{validation.table.floorId.NotNull}")
    private Integer floorId;

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
     * Floor-plan shape: SQUARE, ROUND or RECTANGLE. Defaults to SQUARE.
     */
    private String shape;

    /**
     * Horizontal position on the floor plan, in percent (0-100).
     */
    private Integer posX;

    /**
     * Vertical position on the floor plan, in percent (0-100).
     */
    private Integer posY;

    /**
     * Width on the floor plan, in percent (0-100).
     */
    private Integer width;

    /**
     * Height on the floor plan, in percent (0-100).
     */
    private Integer height;

    /**
     * Whether the table is active at the cashier.
     */
    private Boolean active;

    /**
     * Converts this DTO to a Table entity.
     *
     * @return a Table entity with storeId, name and capacity set
     */
    public Table toTable() {
        return new Table()
                .setStoreId(storeId)
                .setFloorId(floorId)
                .setName(name)
                .setCapacity(capacity)
                .setShape(shape == null ? "SQUARE" : shape)
                .setPosX(posX == null ? 10 : posX)
                .setPosY(posY == null ? 10 : posY)
                .setWidth(width == null ? 12 : width)
                .setHeight(height == null ? 12 : height)
                .setActive(active == null || active);
    }
}