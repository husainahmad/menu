package com.harmoni.pos.menu.model.dto.kitchen;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Product-to-station routing payload. Takes priority over category routing.
 */
@Data
public class KitchenProductRoutingDto {

    @NotNull(message = "{validation.kitchenRouting.productId.NotNull}")
    private Integer productId;

    @NotNull(message = "{validation.kitchenRouting.stationId.NotNull}")
    private Integer kitchenStationId;
}
