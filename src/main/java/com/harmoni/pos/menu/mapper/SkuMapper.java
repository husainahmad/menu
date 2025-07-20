package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Sku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Sku entity database operations.
 */
@Mapper
public interface SkuMapper {

    /**
     * Deletes a Sku by its ID.
     * @param id the Sku ID
     * @return number of rows affected
     */
    int deleteById(Integer id);

    /**
     * Deletes Skus by Product ID.
     * @param row the Sku object
     * @return number of rows affected
     */
    int deleteByProductId(Sku row);

    /**
     * Inserts a new Sku.
     * @param row the Sku object
     * @return number of rows affected
     */
    int insert(Sku row);

    /**
     * Selects a Sku by its ID.
     * @param id the Sku ID
     * @return the Sku object
     */
    Sku selectById(Integer id);

    /**
     * Selects a Sku by its name and product ID.
     * @param name the Sku name
     * @param productId the Product ID
     * @return the Sku object
     */
    Sku selectByNameProductId(String name, Integer productId);

    /**
     * Selects all Skus.
     * @return list of Sku objects
     */
    List<Sku> selectAll();

    /**
     * Updates a Sku by its primary key.
     * @param row the Sku object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Sku row);

    /**
     * Inserts or updates a Sku.
     * @param row the Sku object
     * @return number of rows affected
     */
    int insertOrUpdate(Sku row);

    /**
     * Selects Skus by Product ID.
     * @param productId the Product ID
     * @return list of Sku objects
     */
    List<Sku> selectByProductId(Integer productId);

    /**
     * Selects Skus by a list of IDs.
     * @param ids list of Sku IDs
     * @return list of Sku objects
     */
    List<Sku> selectByIds(List<Integer> ids);

    /**
     * Selects Sku prices by a list of IDs and Tier ID.
     * @param ids list of Sku IDs
     * @param tierId the Tier ID
     * @return list of Sku objects with price
     */
    List<Sku> selectPriceByIdsAndTierId(List<Integer> ids, Integer tierId);

}