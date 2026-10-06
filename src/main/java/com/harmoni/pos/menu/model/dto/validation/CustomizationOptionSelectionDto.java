package com.harmoni.pos.menu.model.dto.validation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * One customization option chosen within a group, and how many of it.
 * <p>
 * The choice is named, never priced. What the option costs is read from this service's
 * catalogue, because a caller that could name a price would not need to ask.
 */
@Data
public class CustomizationOptionSelectionDto {

    /**
     * The customization option being chosen.
     */
    @NotNull(message = "{validation.orderValidation.customizationOptionId.NotNull}")
    private Integer customizationOptionId;

    /**
     * How many selections of this option the line carries. A customer who wants three
     * extra shots sends a quantity of 3 rather than naming the option three times.
     * <p>
     * Defaults to one so callers that only care about the choice itself do not have to
     * spell it out. Whether the count breaks the customization's own limit is a selling
     * rule decided here, not a malformed request.
     */
    @NotNull
    @Min(1)
    private Integer quantity = 1;
}
