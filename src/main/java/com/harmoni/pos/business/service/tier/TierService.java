package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;

import java.util.List;

/**
 * Service interface for managing Tier entities.
 */
public interface TierService {

    /**
     * Creates a new Tier.
     *
     * @param tierDto the data transfer object containing tier details
     * @return the ID of the created Tier
     */
    int create(TierDto tierDto);

    /**
     * Updates an existing Tier.
     *
     * @param tierDto the data transfer object containing updated tier details
     * @param id the ID of the Tier to update
     * @return true if the update was successful, false otherwise
     */
    boolean update(TierDto tierDto, Integer id);

    /**
     * Deletes a Tier by its ID.
     *
     * @param id the ID of the Tier to delete
     * @return the number of records deleted
     */
    int delete(Integer id);

    /**
     * Retrieves a Tier by its ID.
     *
     * @param id the ID of the Tier to retrieve
     * @return the Tier object, or null if not found
     */
    Tier get(Integer id);

    /**
     * Retrieves all Tiers for a given brand ID.
     *
     * @param id the brand ID
     * @return a list of Tiers associated with the brand
     */
    List<Tier> getByBrandId(Integer id);

    /**
     * Retrieves all Tiers for a given brand ID and tier type.
     *
     * @param id the brand ID
     * @param tierType the type of Tier
     * @return a list of Tiers matching the criteria
     */
    List<Tier> getByBrandIdAndTierType(Integer id, TierType tierType);

    /**
     * Validates Tiers by their IDs.
     *
     * @param ids the list of Tier IDs to validate
     * @return a list of valid Tiers
     */
    List<Tier> validateTierByIds(List<Integer> ids);

}
