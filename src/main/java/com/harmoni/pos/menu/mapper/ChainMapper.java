package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Chain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChainMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Chain row);
    Chain selectByPrimaryKey(Integer id);
    Chain selectByName(String name);
    List<Chain> selectAll();
    int updateByPrimaryKey(Chain row);
    List<Chain> selectByBrandId(Integer brandId);
}