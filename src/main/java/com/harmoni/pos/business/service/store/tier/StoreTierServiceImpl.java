package com.harmoni.pos.business.service.store.tier;

import com.harmoni.pos.menu.mapper.StoreTierMapper;
import com.harmoni.pos.menu.model.StoreTier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link StoreTierService} to manage {@link StoreTier} entities.
 */
@RequiredArgsConstructor
@Service("storeTierService")
public class StoreTierServiceImpl implements StoreTierService {

    private final StoreTierMapper storeTierMapper;

    /**
     * Inserts a new {@link StoreTier} record.
     *
     * @param row the {@link StoreTier} entity to insert
     * @return the number of rows affected
     */
    @Override
    public int insert(StoreTier row) {
        return storeTierMapper.insert(row);
    }

    /**
     * Retrieves a {@link StoreTier} by its primary key ID.
     *
     * @param id the primary key ID
     * @return the {@link StoreTier} entity or null if not found
     */
    @Override
    public StoreTier selectByPrimaryKey(Integer id) {
        return storeTierMapper.selectByPrimaryKey(id);
    }

    /**
     * Retrieves a {@link StoreTier} by the store ID.
     *
     * @param storeId the store ID to query by
     * @return the {@link StoreTier} entity or null if not found
     */
    @Override
    public StoreTier selectByStoreId(Integer storeId) {
        return storeTierMapper.selectByStoreId(storeId);
    }

    /**
     * Inserts or updates a {@link StoreTier} record based on the store ID.
     * If a record exists for the store ID, it is updated; otherwise, inserted.
     *
     * @param row the {@link StoreTier} entity to insert or update
     * @return the number of rows affected
     */
    @Override
    public int insertOrUpdateByStoreId(StoreTier row) {
        return storeTierMapper.insertOrUpdateByStoreId(row);
    }
}
