package com.harmoni.pos.menu.mapper;


import com.harmoni.pos.menu.model.Brand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BrandMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Brand row);

    Brand selectByPrimaryKey(Integer id);
    Brand selectByName(String name);

    List<Brand> selectAll();
    int updateByPrimaryKey(Brand row);
}