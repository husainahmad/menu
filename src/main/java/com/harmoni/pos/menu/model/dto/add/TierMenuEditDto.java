package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.dto.edit.CategoryEditDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for editing Tier Menu associations.
 */
@Data
public class TierMenuEditDto {
    /**
     * The tier to be edited.
     */
    @NotNull(message = "{validation.tier.NotNull}")
    @JsonProperty("tier")
    private @Valid TierEditDto tierDto;

    /**
     * The category to be edited.
     */
    @NotNull(message = "{validation.category.NotNull}")
    @JsonProperty("category")
    private CategoryEditDto categoryDto;

    /**
     * Indicates if the association is active.
     */
    private Boolean active;
}
