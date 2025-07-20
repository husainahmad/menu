package com.harmoni.pos.transaction.mapper;

import com.harmoni.pos.transaction.model.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for handling operations on the Order table.
 */
@Mapper
public interface OrderMapper {

    /**
     * Deletes an order from the database by its primary key.
     *
     * @param id the ID of the order to delete
     * @return the number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new order into the database.
     *
     * @param row the {@link Order} object to insert
     * @return the number of rows affected
     */
    int insert(Order row);

    /**
     * Selects an order from the database by its primary key.
     *
     * @param id the ID of the order to retrieve
     * @return the {@link Order} object, or null if not found
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * Updates an existing order in the database by its primary key.
     *
     * @param row the {@link Order} object containing updated data
     * @return the number of rows affected
     */
    int updateByPrimaryKey(Order row);
}
