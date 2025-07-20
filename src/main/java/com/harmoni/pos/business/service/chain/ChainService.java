package com.harmoni.pos.business.service.chain;

import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;

import java.util.List;

/**
 * Service interface for managing {@link Chain} entities.
 * A chain represents a grouping of stores under a specific brand.
 */
public interface ChainService {

    /**
     * Creates a new chain from the provided data.
     *
     * @param chainDto the DTO containing chain details
     * @return number of records inserted (typically 1 if successful)
     */
    int create(ChainDto chainDto);

    /**
     * Updates an existing chain with the specified ID.
     *
     * @param chainDto the DTO containing updated chain data
     * @param id       the ID of the chain to update
     * @return true if update was successful, false otherwise
     */
    boolean update(ChainDto chainDto, Integer id);

    /**
     * Deletes a chain by its ID.
     *
     * @param id the ID of the chain
     * @return number of records deleted
     */
    int delete(Integer id);

    /**
     * Retrieves a chain by its ID.
     *
     * @param id the ID of the chain
     * @return the chain object, or null if not found
     */
    Chain get(Integer id);

    /**
     * Lists all chains in the system.
     *
     * @return list of all chains
     */
    List<Chain> list();

    /**
     * Lists all chains associated with a specific brand.
     *
     * @param brandId the brand ID
     * @return list of chains under the brand
     */
    List<Chain> listByBrandId(Integer brandId);
}
