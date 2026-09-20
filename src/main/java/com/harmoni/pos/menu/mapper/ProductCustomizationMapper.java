package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.ProductCustomization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MyBatis mapper for {@link ProductCustomization}.
 */
@Mapper
public interface ProductCustomizationMapper {

    /**
     * Inserts a single product-customization link.
     *
     * @param productCustomization the link to insert
     * @return number of rows affected
     */
    int insert(ProductCustomization productCustomization);

    /**
     * Inserts multiple product-customization links in a single statement.
     *
     * @param productCustomizations the list of links to insert
     * @return number of rows affected
     */
    int insertBulk(List<ProductCustomization> productCustomizations);

    /**
     * Updates an existing product-customization link by primary key.
     *
     * @param productCustomization the link to update
     * @return number of rows affected
     */
    int updateByPrimaryKey(ProductCustomization productCustomization);

    /**
     * Updates only the override configuration (required / min / max selection and sort order)
     * of a product-customization link by its primary key.
     *
     * @param productCustomization the link holding the configuration to update
     * @return number of rows affected
     */
    int updateConfigurationById(ProductCustomization productCustomization);

    /**
     * Deletes a product-customization link by its primary key.
     *
     * @param id the link's primary key
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Deletes all product-customization links for a given product.
     *
     * @param productId the product ID whose links should be removed
     * @return number of rows affected
     */
    int deleteByProductId(Integer productId);

    /**
     * Retrieves a product-customization link by its primary key.
     *
     * @param id the link's primary key
     * @return the link, or null if not found
     */
    ProductCustomization selectByPrimaryKey(Integer id);

    /**
     * Retrieves all non-deleted product-customization links for a single product.
     *
     * @param productId the product ID
     * @return list of links for the product
     */
    List<ProductCustomization> selectByProductId(Integer productId);

    /**
     * Retrieves all non-deleted product-customization links for a list of products.
     * Used for batch-loading customization assignments across paginated result sets.
     *
     * @param productIds list of product IDs to query
     * @return list of links for the given products
     */
    List<ProductCustomization> selectByProductIds(List<Integer> productIds);
}
