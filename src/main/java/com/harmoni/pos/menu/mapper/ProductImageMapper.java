package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.ProductImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for ProductImage entity database operations.
 */
@Mapper
public interface ProductImageMapper {
    /**
     * Deletes a ProductImage by its primary key.
     * @param id the ProductImage ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new ProductImage.
     * @param row the ProductImage object
     * @return number of rows affected
     */
    int insert(ProductImage row);

    /**
     * Selects a ProductImage by its primary key.
     * @param id the ProductImage ID
     * @return the ProductImage object
     */
    ProductImage selectByPrimaryKey(Integer id);

    /**
     * Selects a ProductImage by product ID.
     * @param productId the Product ID
     * @return the ProductImage object
     */
    ProductImage selectByProductKey(Integer productId);

    /**
     * Updates a ProductImage by its primary key.
     * @param productImage the ProductImage object
     * @return number of rows affected
     */
    int updateByPrimaryKey(ProductImage productImage);

    /**
     * Updates the image of a ProductImage by product ID.
     * @param productImage the ProductImage object
     * @return number of rows affected
     */
    int updateImageByProductKey(ProductImage productImage);

    /**
     * Updates the product ID of a ProductImage by its primary key.
     * @param productImage the ProductImage object
     * @return number of rows affected
     */
    int updateProductIdByPrimaryKey(ProductImage productImage);
}