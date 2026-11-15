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
     * Selects a Table by its name, floor ID and store ID.
     * @param name the Table name
     * @param floorId the Floor ID
     * @param storeId the Store ID
     * @return the Table object
     */
    Table selectByNameFloorIdStoreId(String name, Integer floorId, Integer storeId);

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
     * Selects Tables by Floor ID.
     * @param floorId the Floor ID
     * @return list of Table objects
     */
    List<Table> selectByFloorId(Integer floorId);

    /**
     * Counts the tables on a floor, used to guard floor deletion.
     * @param floorId the Floor ID
     * @return number of active tables on the floor
     */
    int countByFloorId(Integer floorId);

    /**
     * Updates a Table by its primary key.
     * @param row the Table object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Table row);
}