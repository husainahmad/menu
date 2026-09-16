package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Table;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Table entity database operations.
 */
@Mapper
public interface TableMapper {

    /**
     * Soft deletes a Table by its primary key.
     * @param row the Table object with id and deletedAt set
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Table row);

    /**
     * Inserts a new Table.
     * @param row the Table object
     * @return number of rows affected
     */
    int insert(Table row);

    /**
     * Selects a Table by its primary key.
     * @param id the Table ID
     * @return the Table object
     */
    Table selectByPrimaryKey(Integer id);

    /**
     * Selects a Table by its name and store ID.
     * @param name the Table name
     * @param storeId the Store ID
     * @return the Table object
     */
    Table selectByNameStoreId(String name, Integer storeId);

    /**
     * Selects all Tables.
     * @return list of Table objects
     */
    List<Table> selectAll();

    /**
     * Selects Tables by Store ID.
     * @param storeId the Store ID
     * @return list of Table objects
     */
    List<Table> selectByStoreId(Integer storeId);

    /**
     * Updates a Table by its primary key.
     * @param row the Table object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Table row);
}