package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for TierService entity database operations.
 */
@Mapper
public interface TierServiceMapper {

    /**
     * Deletes a TierService by its primary key.
     * @param id the TierService ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Deletes TierServices by Tier ID.
     * @param tierId the Tier ID
     * @return number of rows affected
     */
    int deleteByTierId(Integer tierId);

    /**
     * Inserts a new TierService.
     * @param row the TierService object
     * @return number of rows affected
     */
    int insert(TierService row);

    /**
     * Selects a TierService by its primary key.
     * @param id the TierService ID
     * @return the TierService object
     */
    TierService selectByPrimaryKey(Integer id);

    /**
     * Selects TierServices by Brand ID.
     * @param id the Brand ID
     * @return list of TierService objects
     */
    List<TierService> selectByBrandId(Integer id);

    /**
     * Updates a TierService by its primary key.
     * @param row the TierService object
     * @return number of rows affected
     */
    int updateByPrimaryKey(TierService row);

    /**
     * Updates TierServices in bulk.
     * @param tierServices list of TierService objects
     * @return number of rows affected
     */
    int updateTierServicesBulk(List<TierService> tierServices);
}