package com.harmoni.pos.transaction.mapper;

import com.harmoni.pos.transaction.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Order row);
    Order selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Order row);

}