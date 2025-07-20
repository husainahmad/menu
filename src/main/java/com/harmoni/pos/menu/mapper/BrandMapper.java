package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Brand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Brand entity database operations.
 */
@Mapper
public interface BrandMapper {

    /**
     * Deletes a Brand by its primary key.
     * @param id the Brand ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new Brand.
     * @param row the Brand object
     * @return number of rows affected
     */
    int insert(Brand row);

    /**
     * Selects a Brand by its primary key.
     * @param id the Brand ID
     * @return the Brand object
     */
    Brand selectByPrimaryKey(Integer id);

    /**
     * Selects a Brand by its name.
     * @param name the Brand name
     * @return the Brand object
     */
    Brand selectByName(String name);

    /**
     * Selects all Brands.
     * @return list of Brand objects
     */
    List<Brand> selectAll();

    /**
     * Updates a Brand by its primary key.
     * @param row the Brand object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Brand row);
}