package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.StoreTier;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for StoreTier entity database operations.
 */
@Mapper
public interface StoreTierMapper {

    /**
     * Inserts a new StoreTier.
     * @param row the StoreTier object
     * @return number of rows affected
     */
    int insert(StoreTier row);

    /**
     * Selects a StoreTier by its primary key.
     * @param id the StoreTier ID
     * @return the StoreTier object
     */
    StoreTier selectByPrimaryKey(Integer id);

    /**
     * Selects a StoreTier by Store ID.
     * @param storeId the Store ID
     * @return the StoreTier object
     */
    StoreTier selectByStoreId(Integer storeId);

    /**
     * Inserts or updates a StoreTier by Store ID.
     * @param row the StoreTier object
     * @return number of rows affected
     */
    int insertOrUpdateByStoreId(StoreTier row);

}