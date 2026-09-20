package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.SelectionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Data transfer object for Customization.
 * Used for transferring customization data between layers.
 */
@Data
public class CustomizationDto {

    /**
     * The customization ID. Present when updating an existing customization.
     */
    private Integer id;

    /**
     * The name of the customization.
     */
    @NotBlank(message = "{validation.customization.name.NotBlank}")
    private String name;

    /**
     * The description of the customization.
     */
    private String description;

    /**
     * The minimum number of options that must be selected.
     */
    private Integer minimumSelection;

    /**
     * The maximum number of options that can be selected.
     */
    private Integer maximumSelection;

    /**
     * Indicates if the customization is active.
     */
    private Boolean active;

    /**
     * Indicates if the customization is required.
     */
    private Boolean required;

    private SelectionType selectionType;

    private List<CustomizationOptionDto> customizationOptions;
    /**
     * Converts this DTO to a Customization entity.
     *
     * @return a Customization entity with name, description, and brandId set
     */
    public Customization toEntity() {
        return new Customization()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setMinimumSelection(minimumSelection)
                .setMaximumSelection(maximumSelection)
                .setRequired(required)
                .setSelectionType(selectionType)
                .setCustomizationOptions(customizationOptions == null
                        ? Collections.emptyList()
                        : customizationOptions.stream().map(CustomizationOptionDto::toEntity).toList());
    }
}
