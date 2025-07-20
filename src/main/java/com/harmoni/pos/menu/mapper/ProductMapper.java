package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Mapper interface for Product entity database operations.
 */
@Mapper
public interface ProductMapper {

    /**
     * Deletes a Product by its primary key.
     * @param id the Product ID
     * @param deleted flag indicating if the product is deleted
     * @param deletedAt the deletion timestamp
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id, Boolean deleted, Date deletedAt);

    /**
     * Inserts a new Product.
     * @param row the Product object
     * @return number of rows affected
     */
    int insert(Product row);

    /**
     * Selects a Product by its primary key.
     * @param id the Product ID
     * @return the Product object
     */
    Product selectByPrimaryKey(Integer id);

    /**
     * Selects a Product by its name and category ID.
     * @param name the Product name
     * @param categoryId the Category ID
     * @return the Product object
     */
    Product selectByNameCategoryId(String name, Integer categoryId);

    /**
     * Selects all Products.
     * @return list of Product objects
     */
    List<Product> selectAll();

    /**
     * Selects Products by a list of IDs and brand ID.
     * @param ids list of Product IDs
     * @param brandId the Brand ID
     * @return list of Product objects
     */
    List<Product> selectByIds(List<Integer> ids, Integer brandId);

    /**
     * Selects Products by Category ID.
     * @param categoryId the Category ID
     * @return list of Product objects
     */
    List<Product> selectByCategoryId(Integer categoryId);

    /**
     * Selects Products by Category ID and Tier ID with price.
     * @param categoryId the Category ID
     * @param tierId the Tier ID
     * @return list of Product objects
     */
    List<Product> selectByCategoryIdPrice(Integer categoryId, Integer tierId);

    /**
     * Selects Products by Category ID, Brand ID, and search term.
     * @param categoryId the Category ID
     * @param brandId the Brand ID
     * @param search the search term
     * @return list of Product objects
     */
    List<Product> selectByCategoryIdBrandId(Integer categoryId, Integer brandId, String search);

    /**
     * Updates a Product by its primary key.
     * @param row the Product object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Product row);

}