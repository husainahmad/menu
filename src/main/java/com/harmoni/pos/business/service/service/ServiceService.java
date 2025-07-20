package com.harmoni.pos.business.service.service;

import com.harmoni.pos.menu.model.Service;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import java.util.List;

/**
 * Service interface for managing business services (e.g., additional services offered in POS).
 * Provides methods for creating and retrieving service-related data.
 */
public interface ServiceService {

    /**
     * Creates a new service record based on the provided DTO.
     *
     * @param serviceDto the service data to be saved
     * @return the generated ID or the number of rows affected
     */
    int create(ServiceDto serviceDto);

    /**
     * Retrieves all service records including their sub-services, if any.
     *
     * @return list of {@link Service} objects with associated sub-service information
     */
    List<Service> getAllWithSub();
}
