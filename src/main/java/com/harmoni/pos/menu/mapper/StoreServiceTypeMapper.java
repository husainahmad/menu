package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.StoreServiceType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreServiceTypeMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(StoreServiceType row);
    StoreServiceType selectByPrimaryKey(Integer id);
    StoreServiceType selectByStoreIdSubServiceId(Integer storeId, Integer subServiceId);
    int updateByPrimaryKey(StoreServiceType row);
    List<StoreServiceType> selectByStoreId(Integer storeId);

}