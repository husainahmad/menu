package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Store.
 * Used for transferring store data between layers.
 */
@Data
public class StoreDto {

    /**
     * The name of the store.
     */
    @NotBlank(message = "{validation.store.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The chain ID associated with the store.
     */
    @NotNull(message = "{validation.store.chainId.NotNull}")
    private Integer chainId;

    /**
     * The address of the store.
     */
    @NotBlank(message = "{validation.store.address.NotBlank}")
    private String address;

    /**
     * The tier menu ID for the store.
     */
    @NotNull(message = "{validation.store.tier.menu.id.NotNull}")
    private Integer tierMenuId;

    /**
     * The tier price ID for the store.
     */
    @NotNull(message = "{validation.store.tier.price.id.NotNull}")
    private Integer tierPriceId;

    /**
     * The tier service ID for the store.
     */
    @NotNull(message = "{validation.store.tier.service.id.NotNull}")
    private Integer tierServiceId;

    /**
     * Converts this DTO to a Store entity.
     *
     * @return a Store entity with fields set from this DTO
     */
    public Store toStore() {
        return new Store()
                .setName(name)
                .setChainId(chainId)
                .setAddress(address)
                .setTierPriceId(tierPriceId)
                .setTierServiceId(tierServiceId)
                .setTierMenuId(tierMenuId);
    }

    /**
     * Converts this DTO to a StoreTier entity.
     *
     * @return a StoreTier entity with tier IDs set from this DTO
     */
    public StoreTier toStoreTier() {
        return new StoreTier()
                .setTierPriceId(tierPriceId)
                .setTierMenuId(tierMenuId)
                .setTierServiceId(tierServiceId);
    }

}