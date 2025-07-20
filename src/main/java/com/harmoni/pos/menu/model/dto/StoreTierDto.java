package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.StoreTier;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for Store Tier.
 * Used for transferring store tier data between layers.
 */
@Data
public class StoreTierDto {

    /**
     * The store ID.
     */
    @NotNull(message = "{validation.store.id.NotBlank}")
    private Integer storeId;

    /**
     * The tier menu ID for the store.
     */
    private Integer tierMenuId;

    /**
     * The tier service ID for the store.
     */
    private Integer tierServiceId;

    /**
     * The tier price ID for the store.
     */
    private Integer tierPriceId;

    /**
     * Converts this DTO to a StoreTier entity.
     *
     * @return a StoreTier entity with fields set from this DTO
     */
    public StoreTier toStoreTier() {
        return new StoreTier()
                .setStoreId(getStoreId())
                .setTierPriceId(getTierPriceId())
                .setTierMenuId(getTierMenuId())
                .setTierServiceId(getTierServiceId());
    }

}