package com.harmoni.pos.transaction.mapper;


import com.harmoni.pos.transaction.model.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(OrderDetail row);
    OrderDetail selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(OrderDetail row);

}