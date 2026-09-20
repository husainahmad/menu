package com.harmoni.pos.business.service.customizationoptiontierprice;

import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;

import java.util.List;

/**
 * Service interface for managing customization option tier prices.
 * Provides methods for retrieving and upserting customization option tier prices.
 */
public interface CustomizationOptionTierPriceService {

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
     * Inserts or updates a bulk list of customization option tier prices.
     *
     * @param tierPrices list of customization option tier prices to insert or update
     */
    void insertOrUpdateBulk(List<CustomizationOptionTierPrice> tierPrices);
}