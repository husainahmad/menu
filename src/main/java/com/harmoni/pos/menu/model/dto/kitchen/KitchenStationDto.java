package com.harmoni.pos.menu.model.dto.kitchen;

import com.harmoni.pos.menu.model.KitchenStation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create/update payload for a kitchen station.
 */
@Data
public class KitchenStationDto {

    @NotBlank(message = "{validation.kitchenStation.name.NotBlank}")
    @Size(max = 125, message = "{validation.kitchenStation.name.Size}")
    private String name;

    @NotBlank(message = "{validation.kitchenStation.code.NotBlank}")
    @Size(max = 50, message = "{validation.kitchenStation.code.Size}")
    private String code;

    @Size(max = 255, message = "{validation.kitchenStation.description.Size}")
    private String description;

    private String status;

    public KitchenStation toModel() {
        return new KitchenStation()
                .setName(name)
                .setCode(code)
                .setDescription(description)
                .setStatus(status);
    }
}
