package com.harmoni.pos.business.service.storeservicetype;

import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;

/**
 * Service interface for managing Store Service Types.
 */
public interface StoreServiceTypeService {

    /**
     * Creates a new Store Service Type.
     *
     * @param storeServiceTypeDto the data transfer object containing store service type details
     * @return the number of records inserted (usually 1 if successful)
     */
    int create(StoreServiceTypeDto storeServiceTypeDto);

}
