package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Tier entity database operations.
 */
@Mapper
public interface TierMapper {

    /**
     * Deletes a Tier by its primary key.
     * @param id the Tier ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new Tier.
     * @param row the Tier object
     * @return number of rows affected
     */
    int insert(Tier row);

    /**
     * Selects a Tier by its primary key.
     * @param id the Tier ID
     * @return the Tier object
     */
    Tier selectByPrimaryKey(Integer id);

    /**
     * Selects a Tier by its name and brand ID.
     * @param name the Tier name
     * @param brandId the Brand ID
     * @return the Tier object
     */
    Tier selectByNameAndBrandId(String name, Integer brandId);

    /**
     * Selects Tiers by Brand ID.
     * @param brandId the Brand ID
     * @return list of Tier objects
     */
    List<Tier> selectByBrandId(Integer brandId);

    /**
     * Selects Tiers by Brand ID and Tier Type.
     * @param brandId the Brand ID
     * @param type the TierType
     * @return list of Tier objects
     */
    List<Tier> selectByBrandIdTierType(Integer brandId, TierType type);

    /**
     * Selects Tiers by a list of IDs.
     * @param ids list of Tier IDs
     * @return list of Tier objects
     */
    List<Tier> selectByIds(List<Integer> ids);

    /**
     * Updates a Tier by its primary key.
     * @param row the Tier object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Tier row);

}