package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Floor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Floor entity database operations.
 */
@Mapper
public interface FloorMapper {

    /**
     * Soft deletes a Floor by its primary key.
     * @param row the Floor object with id and deletedAt set
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Floor row);

    /**
     * Inserts a new Floor.
     * @param row the Floor object
     * @return number of rows affected
     */
    int insert(Floor row);

    /**
     * Selects a Floor by its primary key.
     * @param id the Floor ID
     * @return the Floor object
     */
    Floor selectByPrimaryKey(Integer id);

    /**
     * Selects a Floor by its name and store ID.
     * @param name the Floor name
     * @param storeId the Store ID
     * @return the Floor object
     */
    Floor selectByNameStoreId(String name, Integer storeId);

    /**
     * Selects the Floors of one store, ordered by sort order.
     * @param storeId the Store ID
     * @return list of Floor objects
     */
    List<Floor> selectByStoreId(Integer storeId);

    /**
     * Updates a Floor by its primary key.
     * @param row the Floor object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Floor row);
}
