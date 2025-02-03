package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreDto {

    @NotBlank(message = "{validation.store.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.store.chainId.NotNull}")
    private Integer chainId;
    @NotBlank(message = "{validation.store.address.NotBlank}")
    private String address;
    @NotNull(message = "{validation.store.tier.menu.id.NotNull}")
    private Integer tierMenuId;
    @NotNull(message = "{validation.store.tier.price.id.NotNull}")
    private Integer tierPriceId;
    @NotNull(message = "{validation.store.tier.service.id.NotNull}")
    private Integer tierServiceId;

    public Store toStore() {
        return new Store()
                .setName(name)
                .setChainId(chainId)
                .setAddress(address)
                .setTierPriceId(tierPriceId)
                .setTierServiceId(tierServiceId)
                .setTierMenuId(tierMenuId);
    }

    public StoreTier toStoreTier() {
        return new StoreTier()
                .setTierPriceId(tierPriceId)
                .setTierMenuId(tierMenuId)
                .setTierServiceId(tierServiceId);
    }

}