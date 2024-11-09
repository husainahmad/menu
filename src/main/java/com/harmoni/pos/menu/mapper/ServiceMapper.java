package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Service;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServiceMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Service row);
    Service selectByPrimaryKey(Integer id);
    Service selectByName(String name);
    int updateByPrimaryKey(Service row);
    List<Service> selectAllAndSubService();

}