package com.harmoni.pos.business.service.store;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing {@link Store} entities.
 */
public interface StoreService {

    /**
     * Creates a new store record.
     *
     * @param storeDto the data transfer object containing store information to create
     * @return the number of rows affected or the new store ID (depending on implementation)
     */
    int create(StoreDto storeDto);

    /**
     * Deletes a store by its unique identifier.
     *
     * @param id the store ID to delete
     * @return the number of rows affected
     */
    int delete(Integer id);

    /**
     * Retrieves a store by its unique identifier.
     *
     * @param id the store ID
     * @return the {@link Store} entity, or null if not found
     */
    Store get(Integer id);

    /**
     * Retrieves a paginated list of stores filtered by chain ID and an optional search keyword.
     *
     * @param chainId the chain ID to filter stores
     * @param page the page number (starting from 1)
     * @param size the number of records per page
     * @param search an optional search keyword to filter stores by name or other attributes
     * @return a {@link Map} containing pagination metadata and list of stores, keys may include "page", "size", "total", "data", etc.
     */
    Map<String, Object> getAllStoresByChainIdPaginated(Integer chainId, int page, int size, String search);

    /**
     * Retrieves all stores by chain ID filtered by an optional search keyword without pagination.
     *
     * @param chainId the chain ID to filter stores
     * @param search an optional search keyword to filter stores
     * @return a list of {@link Store} entities matching the criteria
     */
    List<Store> getAllStoresByChainId(Integer chainId, String search);

    /**
     * Updates store information for the given store ID.
     *
     * @param id the store ID to update
     * @param storeDto the data transfer object containing updated store information
     * @return the number of rows affected
     */
    int update(Integer id, StoreDto storeDto);
}
