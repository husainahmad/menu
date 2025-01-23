package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.StoreTier;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreTierDto {

    @NotNull(message = "{validation.store.id.NotBlank}")
    private Integer storeId;
    private Integer tierMenuId;
    private Integer tierServiceId;
    private Integer tierPriceId;

    public StoreTier toStoreTier() {
        return new StoreTier()
                .setStoreId(getStoreId())
                .setTierPriceId(getTierPriceId())
                .setTierMenuId(getTierMenuId())
                .setTierServiceId(getTierServiceId());
    }

}