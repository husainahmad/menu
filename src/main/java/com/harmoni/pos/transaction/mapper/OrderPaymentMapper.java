package com.harmoni.pos.transaction.mapper;

import com.harmoni.pos.transaction.model.OrderPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for performing CRUD operations on the OrderPayment table.
 */
@Mapper
public interface OrderPaymentMapper {

    /**
     * Deletes an OrderPayment entry by its primary key.
     *
     * @param id the ID of the OrderPayment to delete
     * @return the number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new OrderPayment entry into the database.
     *
     * @param row the {@link OrderPayment} object to insert
     * @return the number of rows affected
     */
    int insert(OrderPayment row);

    /**
     * Retrieves an OrderPayment entry by its primary key.
     *
     * @param id the ID of the OrderPayment to retrieve
     * @return the {@link OrderPayment} object, or null if not found
     */
    OrderPayment selectByPrimaryKey(Integer id);

    /**
     * Updates an existing OrderPayment entry in the database.
     *
     * @param row the {@link OrderPayment} object containing updated values
     * @return the number of rows affected
     */
    int updateByPrimaryKey(OrderPayment row);
}
