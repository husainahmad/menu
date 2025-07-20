package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * Data transfer object for Tier Service.
 * Used for transferring tier service data between layers.
 */
@Data
public class TierServiceDto {

    /**
     * The tier to associate with the service.
     */
    @JsonProperty("tier")
    private @Valid TierEditDto tierDto;

    /**
     * The subservice to associate with the tier.
     */
    @JsonProperty("subService")
    private @Valid SubServiceDto subServiceDto;

    /**
     * Indicates if the tier service is active.
     */
    @JsonProperty("active")
    private boolean active;

    /**
     * Converts this DTO to a TierService entity.
     *
     * @return a TierService entity with tierId, subServiceId, and active set
     */
    public TierService toTierService() {
        return new TierService()
                .setTierId(tierDto.getId())
                .setSubServiceId(subServiceDto.getId())
                .setActive(active);
    }

}