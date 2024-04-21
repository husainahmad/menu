package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.SubService;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubServiceMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(SubService row);
    SubService selectByPrimaryKey(Integer id);
    SubService selectByNameServiceId(String name, Integer serviceId);
    int updateByPrimaryKey(SubService row);

}