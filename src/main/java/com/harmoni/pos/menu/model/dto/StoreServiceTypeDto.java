package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.StoreServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreServiceTypeDto {

    @NotNull(message = "{validation.storeServiceType.storeId.NotNull}")
    private Integer storeId;
    @NotNull(message = "{validation.storeServiceType.subServiceId.NotNull}")
    private Integer subServiceId;

    public StoreServiceType toStoreServiceType() {
        return new StoreServiceType()
                .setStoreId(storeId)
                .setSubServiceId(subServiceId);
    }
}