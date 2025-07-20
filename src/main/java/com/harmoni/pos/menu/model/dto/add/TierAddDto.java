package com.harmoni.pos.menu.model.dto.add;

import com.harmoni.pos.menu.model.dto.TierDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for adding a new Tier.
 * Inherits properties from {@link TierDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TierAddDto extends TierDto {
}