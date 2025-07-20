package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
/**
 * Mapper interface for Product entity database operations.
 */

@Mapper
public interface SkuTierPriceMapper {
    /**
     * Deletes a Product by its primary key.
     * @param id the Product ID
     * @param deleted flag indicating if the product is deleted
     * @param deletedAt the deletion timestamp
     * @return number of rows affected
     */


    /**
     * Inserts a new Product.
     * @param row the Product object
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Selects a Product by its primary key.
     * @param id the Product ID
     * @return the Product object
     */
    int deleteBySkuId(Integer skuId);

    /**
     * Selects a Product by its name and category ID.
     * @param name the Product name
     * @param categoryId the Category ID
     * @return the Product object
     */
    int deleteBySkuIds(List<Sku> skus, Boolean deleted, Date deletedAt);

    /**
     * Selects all Products.
     * @return list of Product objects
     */
    int insert(SkuTierPrice row);

    /**
     * Selects Products by a list of IDs and brand ID.
     * @param ids list of Product IDs
     * @param brandId the Brand ID
     * @return list of Product objects
     */
    int insertOrUpdateSkuTierPrices(List<SkuTierPrice> skuTierPrices);

    /**
     * Selects Products by Category ID.
     * @param categoryId the Category ID
     * @return list of Product objects
     */
    SkuTierPrice selectByPrimaryKey(Integer id);

    /**
     * Selects Products by Category ID and Tier ID with price.
     * @param categoryId the Category ID
     * @param tierId the Tier ID
     * @return list of Product objects
     */
    List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId);

    /**
     * Selects Products by Category ID, Brand ID, and search term.
     * @param categoryId the Category ID
     * @param brandId the Brand ID
     * @param search the search term
     * @return list of Product objects
     */
    int updateByPrimaryKey(SkuTierPrice row);

    /**
     * Updates a Product by its primary key.
     * @param row the Product object
     * @return number of rows affected
     */
    int insertOrUpdate(SkuTierPrice row);

}