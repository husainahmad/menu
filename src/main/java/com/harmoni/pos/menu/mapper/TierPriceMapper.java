package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierPrice;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for TierPrice entity database operations.
 */
@Mapper
public interface TierPriceMapper {

    /**
     * Deletes a TierPrice by its primary key.
     * @param id the TierPrice ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new TierPrice.
     * @param row the TierPrice object
     * @return number of rows affected
     */
    int insert(TierPrice row);

    /**
     * Selects a TierPrice by its primary key.
     * @param id the TierPrice ID
     * @return the TierPrice object
     */
    TierPrice selectByPrimaryKey(Integer id);

    /**
     * Updates a TierPrice by its primary key.
     * @param row the TierPrice object
     * @return number of rows affected
     */
    int updateByPrimaryKey(TierPrice row);

}