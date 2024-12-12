package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.TierService;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TierServiceDto {

    @JsonProperty("tier")
    private @Valid TierDto tierDto;

    @JsonProperty("subService")
    private SubServiceDto subServiceDto;

    @JsonProperty("isActive")
    private boolean isActive;

    public TierService toTierService() {
        return new TierService()
                .setTierId(tierDto.getId())
                .setSubServiceId(subServiceDto.getId())
                .setActive(isActive);
    }

}