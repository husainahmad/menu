package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.StoreServiceType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for StoreServiceType entity database operations.
 */
@Mapper
public interface StoreServiceTypeMapper {

    /**
     * Deletes a StoreServiceType by its primary key.
     * @param id the StoreServiceType ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new StoreServiceType.
     * @param row the StoreServiceType object
     * @return number of rows affected
     */
    int insert(StoreServiceType row);

    /**
     * Selects a StoreServiceType by its primary key.
     * @param id the StoreServiceType ID
     * @return the StoreServiceType object
     */
    StoreServiceType selectByPrimaryKey(Integer id);

    /**
     * Selects a StoreServiceType by Store ID and SubService ID.
     * @param storeId the Store ID
     * @param subServiceId the SubService ID
     * @return the StoreServiceType object
     */
    StoreServiceType selectByStoreIdSubServiceId(Integer storeId, Integer subServiceId);

    /**
     * Updates a StoreServiceType by its primary key.
     * @param row the StoreServiceType object
     * @return number of rows affected
     */
    int updateByPrimaryKey(StoreServiceType row);

    /**
     * Selects StoreServiceTypes by Store ID.
     * @param storeId the Store ID
     * @return list of StoreServiceType objects
     */
    List<StoreServiceType> selectByStoreId(Integer storeId);

}