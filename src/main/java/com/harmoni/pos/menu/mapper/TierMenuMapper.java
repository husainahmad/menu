package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for TierMenu entity database operations.
 */
@Mapper
public interface TierMenuMapper {

    /**
     * Selects TierMenus by Brand ID.
     * @param id the Brand ID
     * @return list of TierMenu objects
     */
    List<TierMenu> selectByBrandId(Integer id);

    /**
     * Selects TierMenus by Tier ID.
     * @param tierId the Tier ID
     * @return list of TierMenu objects
     */
    List<TierMenu> selectByTierId(Integer tierId);

    /**
     * Updates TierMenus in bulk.
     * @param tierMenus list of TierMenu objects
     * @return number of rows affected
     */
    int updateTierMenuBulk(List<TierMenu> tierMenus);
}