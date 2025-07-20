package com.harmoni.pos.business.service.subservice;

import com.harmoni.pos.menu.model.dto.SubServiceDto;

/**
 * Service interface for managing SubService entities.
 */
public interface SubServiceService {

    /**
     * Creates a new SubService entity based on the provided data transfer object.
     *
     * @param subServiceDto the DTO containing SubService details to be created
     * @return the number of records inserted (usually 1 if successful)
     */
    int create(SubServiceDto subServiceDto);

}
