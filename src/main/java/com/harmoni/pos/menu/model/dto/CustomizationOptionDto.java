package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.CustomizationOption;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for CustomizationOption.
 * Used for transferring customization option data between layers.
 */
@Data
public class CustomizationOptionDto {

    /**
     * The name of the customization option.
     */
    @NotBlank(message = "{validation.customizationOption.name.NotBlank}")
    private String name;

    /**
     * The ID of the customization option. Present when updating an existing option.
     */
    private Integer id;

    /**
     * The ID of the customization this option belongs to.
     */
    private Integer customizationId;

    /**
     * The tier-based prices for this option.
     */
    private List<CustomizationOptionTierPriceDto> tierPrices;

    /**
     * Converts this DTO to a CustomizationOption entity.
     *
     * @return a CustomizationOption entity
     */
    public CustomizationOption toEntity() {
        CustomizationOption option = new CustomizationOption()
                .setId(id)
                .setName(name)
                .setCustomizationId(customizationId);
        if (tierPrices != null) {
            option.setTierPrices(tierPrices.stream().map(CustomizationOptionTierPriceDto::toEntity).toList());
        }
        return option;
    }
}
