package com.harmoni.pos.transaction.mapper;

import com.harmoni.pos.transaction.model.OrderPayment;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrderPaymentMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(OrderPayment row);
    OrderPayment selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(OrderPayment row);

}