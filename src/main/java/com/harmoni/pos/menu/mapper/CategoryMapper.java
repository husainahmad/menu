package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for Category entity database operations.
 */
@Mapper
public interface CategoryMapper {

    /**
     * Deletes a Category by its primary key.
     * @param id the Category ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new Category.
     * @param row the Category object
     * @return number of rows affected
     */
    int insert(Category row);

    /**
     * Selects a Category by its primary key.
     * @param id the Category ID
     * @return the Category object
     */
    Category selectByPrimaryKey(Integer id);

    /**
     * Selects a Category by its name and brand ID.
     * @param name the Category name
     * @param brandId the Brand ID
     * @return the Category object
     */
    Category selectByNameBrandId(String name, Integer brandId);

    /**
     * Selects Categories by a list of category IDs.
     * @param categoryIds list of Category IDs
     * @return list of Category objects
     */
    List<Category> selectByListCategories(List<Integer> categoryIds);

    /**
     * Selects Categories by Brand ID.
     * @param brandId the Brand ID
     * @return list of Category objects
     */
    List<Category> selectByBrandId(Integer brandId);

    /**
     * Updates a Category by its primary key.
     * @param row the Category object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Category row);

    /**
     * Search categories by name LIKE (for AI).
     * @param categoryName the category name keyword
     * @return list of matching categories
     */
    List<Category> searchByCategoryName(@Param("categoryName") String categoryName);

}