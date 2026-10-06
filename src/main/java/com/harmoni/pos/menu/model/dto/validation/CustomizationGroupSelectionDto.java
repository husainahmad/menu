package com.harmoni.pos.menu.model.dto.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * One customization group chosen on an order line, with its selected options.
 * <p>
 * Grouping options by their parent customization lets each group's rules (required,
 * single-select, min/max) be validated independently. Only IDs and quantities appear
 * here; names and prices are answered from the catalogue.
 */
@Data
public class CustomizationGroupSelectionDto {

    /**
     * The customization group being configured, as claimed by the caller. The option's
     * actual group from the catalogue must match, otherwise the choice is refused.
     */
    @NotNull(message = "{validation.orderValidation.customizationId.NotNull}")
    private Integer customizationId;

    /**
     * The chosen options within this group.
     */
    @NotEmpty
    @Valid
    private List<CustomizationOptionSelectionDto> options = new ArrayList<>();
}
