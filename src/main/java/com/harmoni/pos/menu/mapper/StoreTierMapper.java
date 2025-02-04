package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.StoreTier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreTierMapper {

    int insert(StoreTier row);
    StoreTier selectByPrimaryKey(Integer id);
    StoreTier selectByStoreId(Integer id);
    int insertOrUpdateByStoreId(StoreTier row);

}