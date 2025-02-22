package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TierMenuMapper {
    List<TierMenu> selectByBrandId(Integer id);
    List<TierMenu> selectByTierId(Integer tierId);
    int updateTierMenuBulk(List<TierMenu> tierMenus);
}