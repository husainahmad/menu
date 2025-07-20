package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data transfer object for editing Tier Menu associations.
 * Inherits properties from {@link TierEditDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TierMenuEditDto extends TierEditDto {

    /**
     * List of categories to associate with the tier.
     */
    @JsonProperty("categories")
    private List<CategoryEditDto> categoryDto;
}
