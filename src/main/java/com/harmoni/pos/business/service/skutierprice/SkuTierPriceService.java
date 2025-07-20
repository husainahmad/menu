package com.harmoni.pos.business.service.skutierprice;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;

import java.util.Date;
import java.util.List;

/**
 * Service interface for managing SKU Tier Prices.
 * Provides methods for creating, retrieving, updating, and deleting SKU tier prices.
 */
public interface SkuTierPriceService {

    /**
     * Creates a new SKU tier price from the given DTO.
     *
     * @param skuTierPriceDto the DTO containing SKU tier price data
     * @return number of records inserted (should be 1 if successful)
     */
    int create(SkuTierPriceDto skuTierPriceDto);

    /**
     * Selects SKU tier prices by a list of SKU IDs and a tier ID.
     *
     * @param skuIds list of SKU IDs to query
     * @param tierId the tier ID for filtering prices
     * @return list of SKU tier prices matching the SKUs and tier
     */
    List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId);

    /**
     * Inserts or updates a bulk list of SKU tier prices.
     *
     * @param skuTierPrices list of SKU tier prices to insert or update
     */
    void insetOrUpdateBulk(List<SkuTierPrice> skuTierPrices);

    /**
     * Deletes SKU tier prices by SKU ID.
     *
     * @param skuId the SKU ID whose tier prices should be deleted
     * @return number of records deleted
     */
    int deleteBySkuId(Integer skuId);

    /**
     * Deletes SKU tier prices by a list of SKUs, marking them as deleted with provided flags.
     *
     * @param skus    list of SKUs whose tier prices should be deleted
     * @param deleted deletion flag to set
     * @param deletedAt deletion timestamp to set
     * @return number of records deleted
     */
    int deleteBySkuIds(List<Sku> skus, Boolean deleted, Date deletedAt);
}
