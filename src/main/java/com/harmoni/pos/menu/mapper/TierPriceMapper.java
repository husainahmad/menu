package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.TierPrice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TierPriceMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(TierPrice row);
    TierPrice selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(TierPrice row);

}