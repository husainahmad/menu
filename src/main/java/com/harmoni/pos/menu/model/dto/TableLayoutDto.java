package com.harmoni.pos.menu.model.dto;

import lombok.Data;

/**
 * One table's editable floor-plan state, sent as a batch when the dashboard
 * saves a floor plan.
 *
 * <p>Positions and sizes are percentages of the canvas so the plan scales with
 * the screen. The name, capacity and active flag are included because the same
 * save action persists property-panel edits.</p>
 */
@Data
public class TableLayoutDto {

    /** The table id. */
    private Integer id;

    /** Display name of the table. */
    private String name;

    /** Seating capacity. */
    private Integer capacity;

    /** Whether the table is active at the cashier. */
    private Boolean active;

    /** Shape: SQUARE, ROUND or RECTANGLE. */
    private String shape;

    /** Horizontal position in percent (0-100). */
    private Integer posX;

    /** Vertical position in percent (0-100). */
    private Integer posY;

    /** Width in percent (0-100). */
    private Integer width;

    /** Height in percent (0-100). */
    private Integer height;
}
