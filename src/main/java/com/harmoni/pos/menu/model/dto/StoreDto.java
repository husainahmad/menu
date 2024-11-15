package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreDto {

    @NotBlank(message = "{validation.store.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.store.tierId.NotNull}")
    private Integer tierId;
    @NotNull(message = "{validation.store.chainId.NotNull}")
    private Integer chainId;
    @NotBlank(message = "{validation.store.address.NotBlank}")
    private String address;

    public Store toStore() {
        return new Store()
                .setName(name)
                .setTierId(tierId)
                .setChainId(chainId)
                .setAddress(address);
    }

}