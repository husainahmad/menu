package com.harmoni.pos.menu.model.dto.kitchen;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Category-to-station routing payload.
 */
@Data
public class KitchenCategoryRoutingDto {

    @NotNull(message = "{validation.kitchenRouting.categoryId.NotNull}")
    private Integer categoryId;

    @NotNull(message = "{validation.kitchenRouting.stationId.NotNull}")
    private Integer kitchenStationId;
}
