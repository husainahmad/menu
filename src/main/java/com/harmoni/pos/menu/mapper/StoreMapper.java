package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Store;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreMapper {

    int deleteByPrimaryKey(Long id);
    int insert(Store row);
    Store selectByPrimaryKey(Long id);
    List<Store> selectAllByChainId(Long chainId, String search);
    Store selectByNameChainId(String name, Long chainId);
    int updateByPrimaryKey(Store row);

}