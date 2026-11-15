package com.harmoni.pos.business.service.service;

import com.harmoni.pos.menu.model.Service;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import java.util.List;
import java.util.Map;

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
     * Updates an existing service.
     *
     * @param id         the service ID
     * @param serviceDto the new service details
     * @return the number of rows updated
     */
    int update(Integer id, ServiceDto serviceDto);

    /**
     * Deletes an existing service.
     *
     * @param id the service ID
     * @return the number of rows deleted
     */
    int delete(Integer id);

    /**
     * Retrieves all service records including their sub-services, if any.
     *
     * @return list of {@link Service} objects with associated sub-service information
     */
    List<Service> getAllWithSub();

    /**
     * Retrieves one page of services including their sub-services.
     *
     * @param page the page number, 1-based
     * @param size the number of services per page
     * @return paginated map with service data and metadata
     */
    Map<String, Object> listPaginated(int page, int size);
}
