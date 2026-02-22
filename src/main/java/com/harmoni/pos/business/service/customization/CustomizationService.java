package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.menu.model.Customization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing {@link Customization} entities.
 */
public interface CustomizationService {

    Map<String, Object> listPaginated(String authToken, int page, int size);

    /**
     * Retrieves a customization by its ID.
     *
     * @param id the customization ID
     * @return an optional containing the customization, or empty if not found
     */
    Optional<Customization> getCustomizationById(Integer id);

    /**
     * Retrieves all customizations for a specific brand ID.
     *
     * @param brandId the brand ID
     * @return list of customizations for the brand
     */
    List<Customization> getCustomizationsByBrandId(Integer brandId);

    /**
     * Creates a new customization.
     *
     * @param customization the customization to create
     * @return int if inserted successfully
     */
    int createCustomization(String authHeader, Customization customization);

    /**
     * Updates an existing customization.
     *
     * @param customization the customization with updated data
     * @return int if updated successfully
     */
    int updateCustomization(Customization customization);

    /**
     * Deletes a customization by its ID.
     *
     * @param id the ID of the customization to delete
     * @return int if deleted successfully
     */
    int deleteCustomization(Integer id);
}

