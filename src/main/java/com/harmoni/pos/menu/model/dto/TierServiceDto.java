package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TierServiceDto {

    @JsonProperty("tier")
    private @Valid TierEditDto tierDto;

    @JsonProperty("subService")
    private @Valid SubServiceDto subServiceDto;

    @JsonProperty("active")
    private boolean active;

    public TierService toTierService() {
        return new TierService()
                .setTierId(tierDto.getId())
                .setSubServiceId(subServiceDto.getId())
                .setActive(active);
    }

}