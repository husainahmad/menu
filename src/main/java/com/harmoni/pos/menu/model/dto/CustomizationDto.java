package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.SelectionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for Customization.
 * Used for transferring customization data between layers.
 */
@Data
public class CustomizationDto {

    /**
     * The name of the customization.
     */
    @NotBlank(message = "{validation.customization.name.NotBlank}")
    private String name;

    /**
     * Indicates if the customization is active.
     */
    private Boolean active;

    private SelectionType selectionType;

    private List<CustomizationOptionDto> customizationOptions;
    /**
     * Converts this DTO to a Customization entity.
     *
     * @return a Customization entity with name, description, and brandId set
     */
    public Customization toEntity() {
        return new Customization()
                .setName(name)
                .setSelectionType(selectionType)
                .setCustomizationOptions(customizationOptions.stream().map(CustomizationOptionDto::toEntity).toList());
    }
}
