package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Store;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Store row);
    Store selectByPrimaryKey(Integer id);
    Store selectByNameTierIdChainId(String name, Integer tierId, Integer brandId);
    List<Store> selectAll();
    int updateByPrimaryKey(Store row);

}