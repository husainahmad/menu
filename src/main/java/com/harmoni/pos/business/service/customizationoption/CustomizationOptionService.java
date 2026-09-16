package com.harmoni.pos.business.service.customizationoption;
import com.harmoni.pos.menu.model.CustomizationOption;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link CustomizationOption} entities.
 */
public interface CustomizationOptionService {

    /**
     * Gets a customization option by its ID.
     *
     * @param id the option ID
     * @return optional containing the option if found
     */
    Optional<CustomizationOption> getById(Integer id);

    /**
     * Gets all options for a specific customization ID.
     *
     * @param customizationId the parent customization ID
     * @return list of options
     */
    List<CustomizationOption> getByCustomizationId(Integer customizationId);

    /**
     * Gets all options for a list of customization IDs.
     *
     * @param customizationIds the parent customization IDs
     * @return list of options
     */
    List<CustomizationOption> getByCustomizationIds(List<Integer> customizationIds);

    /**
     * Creates a new customization option.
     *
     * @param option the option to create
     * @return true if created successfully
     */
    boolean create(CustomizationOption option);

    /**
     * Creates multiple customization options in bulk.
     *
     * @param options          the list of options to create
     * @param customizationId the parent customization ID
     * @return number of options created
     */
    int createBulk(List<CustomizationOption> options, Integer customizationId);

    /**
     * Updates an existing customization option.
     *
     * @param option the option to update
     * @return true if updated successfully
     */
    boolean update(CustomizationOption option);

    /**
     * Deletes an option by ID.
     *
     * @param id the option ID
     * @return true if deleted successfully
     */
    boolean delete(Integer id);
}
