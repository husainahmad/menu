package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Store;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Chain entity database operations.
 */
@Mapper
public interface StoreMapper {
    int deleteByPrimaryKey(Integer id);
    /**
     * Deletes a Chain by its primary key.
     * @param id the Chain ID
     * @return number of rows affected
     */
    int insert(Store row);

    /**
     * Inserts a new Chain.
     * @param row the Chain object
     * @return number of rows affected
     */
    Store selectByPrimaryKey(Integer id);

    /**
     * Selects a Chain by its primary key.
     * @param id the Chain ID
     * @return the Chain object
     */
    List<Store> selectAllByChainId(Integer chainId, String search);

    /**
     * Selects a Chain by its name.
     * @param name the Chain name
     * @return the Chain object
     */
    Store selectByNameChainId(String name, Integer chainId);

    /**
     * Selects all Chains.
     * @return list of Chain objects
     */
    int updateByPrimaryKey(Store row);

    /**
     * Updates a Chain by its primary key.
     * @param row the Chain object
     * @return number of rows affected
     */


    /**
     * Selects Chains by Brand ID.
     * @param brandId the Brand ID
     * @return list of Chain objects
     */
}