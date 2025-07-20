package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Chain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Chain entity database operations.
 */
@Mapper
public interface ChainMapper {

    /**
     * Deletes a Chain by its primary key.
     * @param id the Chain ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new Chain.
     * @param row the Chain object
     * @return number of rows affected
     */
    int insert(Chain row);

    /**
     * Selects a Chain by its primary key.
     * @param id the Chain ID
     * @return the Chain object
     */
    Chain selectByPrimaryKey(Integer id);

    /**
     * Selects a Chain by its name.
     * @param name the Chain name
     * @return the Chain object
     */
    Chain selectByName(String name);

    /**
     * Selects all Chains.
     * @return list of Chain objects
     */
    List<Chain> selectAll();

    /**
     * Updates a Chain by its primary key.
     * @param row the Chain object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Chain row);

    /**
     * Selects Chains by Brand ID.
     * @param brandId the Brand ID
     * @return list of Chain objects
     */
    List<Chain> selectByBrandId(Integer brandId);
}