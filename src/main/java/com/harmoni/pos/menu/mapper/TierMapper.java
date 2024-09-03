package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Tier;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TierMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Tier row);
    Tier selectByPrimaryKey(Integer id);
    Tier selectByNameAndBrandId(String name, Integer brandId);
    List<Tier> selectByBrandId(Integer brandId);
    List<Tier> selectByIds(List<Integer> ids);
    int updateByPrimaryKey(Tier row);

}