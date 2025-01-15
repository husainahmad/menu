package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TierServiceMapper {

    int deleteByPrimaryKey(Integer id);
    int deleteByTierId(Integer tierId);
    int insert(TierService row);
    TierService selectByPrimaryKey(Integer id);
    List<TierService> selectByBrandId(Integer id);
    int updateByPrimaryKey(TierService row);
    int updateTierServicesBulk(List<TierService> tierServices);
}