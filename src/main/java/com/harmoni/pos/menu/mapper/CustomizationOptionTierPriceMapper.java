package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for customization option tier price database operations.
 */
@Mapper
public interface CustomizationOptionTierPriceMapper {

    /**
     * Selects a customization option tier price by its primary key.
     *
     * @param id the customization option tier price ID
     * @return the customization option tier price
     */
    CustomizationOptionTierPrice selectByPrimaryKey(Integer id);

    /**
     * Selects all tier prices for the given customization option IDs.
     *
     * @param optionIds list of customization option IDs
     * @return list of customization option tier prices
     */
    List<CustomizationOptionTierPrice> selectByOptionIds(List<Integer> optionIds);

    /**
     * Selects tier prices for the given customization option IDs, filtered by a Tier ID.
     *
     * @param optionIds list of customization option IDs
     * @param tierId the Tier ID
     * @return list of customization option tier prices
     */
    List<CustomizationOptionTierPrice> selectByOptionIdsAndTierId(List<Integer> optionIds, Integer tierId);

    /**
     * Inserts a new customization option tier price.
     *
     * @param row the customization option tier price
     * @return number of rows affected
     */
    int insert(CustomizationOptionTierPrice row);

    /**
     * Inserts a customization option tier price or updates the price on duplicate (Tier, option) key.
     *
     * @param row the customization option tier price
     * @return number of rows affected
     */
    int insertOrUpdate(CustomizationOptionTierPrice row);

    /**
     * Bulk inserts or updates a list of customization option tier prices.
     *
     * @param tierPrices list of customization option tier prices
     * @return number of rows affected
     */
    int insertOrUpdateBulk(List<CustomizationOptionTierPrice> tierPrices);

    /**
     * Updates a customization option tier price by its primary key.
     *
     * @param row the customization option tier price
     * @return number of rows affected
     */
    int updateByPrimaryKey(CustomizationOptionTierPrice row);
}