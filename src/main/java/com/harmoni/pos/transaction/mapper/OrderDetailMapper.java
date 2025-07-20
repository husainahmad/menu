package com.harmoni.pos.transaction.mapper;

import com.harmoni.pos.transaction.model.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    /**
     * Deletes an order detail by its primary key.
     *
     * @param id the ID of the order detail to delete
     * @return the number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new order detail into the database.
     *
     * @param row the {@link OrderDetail} object to insert
     * @return the number of rows affected
     */
    int insert(OrderDetail row);

    /**
     * Retrieves an order detail by its primary key.
     *
     * @param id the ID of the order detail to retrieve
     * @return the {@link OrderDetail} object, or null if not found
     */
    OrderDetail selectByPrimaryKey(Integer id);

    /**
     * Updates an existing order detail identified by its primary key.
     *
     * @param row the {@link OrderDetail} object with updated values
     * @return the number of rows affected
     */
    int updateByPrimaryKey(OrderDetail row);
}
