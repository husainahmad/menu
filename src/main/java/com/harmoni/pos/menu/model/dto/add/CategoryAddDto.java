package com.harmoni.pos.menu.model.dto.add;

import com.harmoni.pos.menu.model.dto.CategoryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for adding a new Tier.
 * Inherits properties from {@link TierDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryAddDto extends CategoryDto {

}
