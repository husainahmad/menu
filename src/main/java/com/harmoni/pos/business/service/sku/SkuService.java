package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;

import java.util.List;

/**
 * Service interface for managing SKUs (Stock Keeping Units).
 * <p>
 * Provides operations for creating, updating, retrieving,
 * validating, and deleting SKUs associated with products.
 * </p>
 */
public interface SkuService {

    /**
     * Creates a new SKU from the given SKU DTO.
     *
     * @param skuDto the SKU data transfer object
     * @return the number of inserted records (typically 1)
     */
    int create(SkuAddDto skuDto);

    /**
     * Creates or updates a list of SKUs.
     *
     * @param skus list of SKUs to create or update
     * @return the list of created or updated SKUs
     */
    List<Sku> createOrUpdate(List<Sku> skus);

    /**
     * Retrieves a list of SKUs belonging to a specific product.
     *
     * @param productId the product ID
     * @return list of SKUs for the given product
     */
    List<Sku> selectByProductId(Integer productId);

    /**
     * Retrieves a list of SKUs by their IDs.
     *
     * @param ids list of SKU IDs
     * @return list of SKUs matching the given IDs
     */
    List<Sku> selectByIds(List<Integer> ids);

    /**
     * Retrieves a list of SKUs by their IDs with pricing information,
     * filtered or adjusted based on the provided JWT token (user context).
     *
     * @param jwtToken JWT token representing user authentication
     * @param ids      list of SKU IDs
     * @return list of SKUs with pricing details
     */
    List<Sku> selectPriceByIds(String jwtToken, List<Integer> ids);

    /**
     * Compares the given list of SKUs with a list of IDs,
     * potentially filtering or validating SKUs against the IDs.
     *
     * @param skus list of SKUs to compare
     * @param ids  list of SKU IDs
     * @return filtered or compared list of SKUs
     */
    List<Sku> compareListSkus(List<Sku> skus, List<Integer> ids);

    /**
     * Updates a list of SKUs in bulk.
     *
     * @param skus list of SKUs to update
     */
    void updateBulk(List<Sku> skus);

    /**
     * Updates a list of SKUs in bulk by their IDs.
     *
     * @param skus list of SKUs to update
     */
    void updateByIdBulk(List<Sku> skus);

    /**
     * Validates that SKU names in the new SKU list do not conflict
     * with the names in the original SKU list.
     *
     * @param originalSkus list of original SKUs
     * @param skuDtos      list of SKUs to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validateSkuName(List<Sku> originalSkus, List<Sku> skuDtos);

    /**
     * Deletes a SKU by its SKU ID.
     *
     * @param skuId the SKU ID to delete
     */
    void deleteSku(Integer skuId);

    /**
     * Deletes all SKUs associated with a given product ID.
     *
     * @param id the product ID whose SKUs should be deleted
     */
    void deleteSkuByProductId(Integer id);

    /**
     * Inserts a new SKU or updates it if it already exists.
     *
     * @param sku the SKU to insert or update
     * @return number of rows affected (typically 1)
     */
    int insertOrUpdate(Sku sku);

    /**
     * Sets SKU IDs in the first list of SKUs based on matching SKUs
     * from the database (second list).
     *
     * @param skus       list of SKUs to update with IDs
     * @param skusFromDB list of SKUs fetched from the database
     * @return list of SKUs with IDs set
     */
    List<Sku> setSkuIdInListSkus(List<Sku> skus, List<Sku> skusFromDB);
}
