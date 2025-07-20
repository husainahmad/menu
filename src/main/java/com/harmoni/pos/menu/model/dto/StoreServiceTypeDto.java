package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.StoreServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for Store Service Type.
 * Used for transferring store service type data between layers.
 */
@Data
public class StoreServiceTypeDto {

    /**
     * The store ID associated with the service type.
     */
    @NotNull(message = "{validation.storeServiceType.storeId.NotNull}")
    private Integer storeId;

    /**
     * The sub-service ID associated with the store.
     */
    @NotNull(message = "{validation.storeServiceType.subServiceId.NotNull}")
    private Integer subServiceId;

    /**
     * Converts this DTO to a StoreServiceType entity.
     *
     * @return a StoreServiceType entity with fields set from this DTO
     */
    public StoreServiceType toStoreServiceType() {
        return new StoreServiceType()
                .setStoreId(storeId)
                .setSubServiceId(subServiceId);
    }
}