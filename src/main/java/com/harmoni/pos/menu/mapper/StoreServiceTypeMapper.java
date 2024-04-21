package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.StoreServiceType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreServiceTypeMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(StoreServiceType row);
    StoreServiceType selectByPrimaryKey(Integer id);
    StoreServiceType selectByStoreIdSubServiceId(Integer storeId, Integer subServiceId);
    int updateByPrimaryKey(StoreServiceType row);

}