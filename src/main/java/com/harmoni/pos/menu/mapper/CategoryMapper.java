package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Category row);
    Category selectByPrimaryKey(Integer id);
    Category selectByNameBrandId(String name, Integer brandId);
    List<Category> selectAll();
    List<Category> selectByBrandId(Integer brandId);
    int updateByPrimaryKey(Category row);

}