package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.SkuTierPrice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuTierPriceMapper {

    int deleteByPrimaryKey(Integer id);
    int deleteBySkuId(Integer skuId);
    int insert(SkuTierPrice row);
    SkuTierPrice selectByPrimaryKey(Integer id);
    List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId);
    int updateByPrimaryKey(SkuTierPrice row);
    int insertOrUpdate(SkuTierPrice row);

}