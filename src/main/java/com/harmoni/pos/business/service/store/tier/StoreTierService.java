package com.harmoni.pos.business.service.store.tier;

import com.harmoni.pos.menu.model.StoreTier;

/**
 * Service interface for managing {@link StoreTier} entities.
 */
public interface StoreTierService {

    /**
     * Inserts a new {@link StoreTier} record into the database.
     *
     * @param row the {@link StoreTier} entity to insert
     * @return the number of rows affected
     */
    int insert(StoreTier row);

    /**
     * Selects a {@link StoreTier} by its primary key ID.
     *
     * @param id the primary key ID of the {@link StoreTier}
     * @return the matching {@link StoreTier} entity, or null if not found
     */
    StoreTier selectByPrimaryKey(Integer id);

    /**
     * Selects a {@link StoreTier} by the store ID.
     *
     * @param storeId the store ID to query by
     * @return the matching {@link StoreTier} entity, or null if not found
     */
    StoreTier selectByStoreId(Integer storeId);

    /**
     * Inserts or updates a {@link StoreTier} record based on the store ID.
     * If a record with the given store ID exists, it will be updated;
     * otherwise, a new record will be inserted.
     *
     * @param row the {@link StoreTier} entity to insert or update
     * @return the number of rows affected
     */
    int insertOrUpdateByStoreId(StoreTier row);
}
