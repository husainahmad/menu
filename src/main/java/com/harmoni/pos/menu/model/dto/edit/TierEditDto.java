package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.TierDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a Tier.
 * Inherits properties from {@link TierDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TierEditDto extends TierDto {
    /**
     * The ID of the tier to edit.
     */
    @NotNull(message = "{validation.tier.id.NotNull}")
    private Integer id;
}