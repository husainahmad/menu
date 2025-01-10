package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TierMenuMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(TierMenu row);
    TierMenu selectByPrimaryKey(Integer id);
    List<TierMenu> selectByBrandId(Integer id);
    int updateByPrimaryKey(TierMenu row);
    int updateTierMenuBulk(List<TierMenu> tierMenus);
}