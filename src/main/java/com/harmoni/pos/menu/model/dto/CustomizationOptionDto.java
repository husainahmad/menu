package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.CustomizationOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
     * The ID of the customization this option belongs to.
     */
    private Integer customizationId;

    /**
     * Converts this DTO to a CustomizationOption entity.
     *
     * @return a CustomizationOption entity
     */
    public CustomizationOption toEntity() {
        return new CustomizationOption()
                .setName(name)
                .setCustomizationId(customizationId);
    }
}
