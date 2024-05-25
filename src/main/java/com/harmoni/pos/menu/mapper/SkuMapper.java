package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Sku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Sku row);
    Sku selectByPrimaryKey(Integer id);
    Sku selectByNameProductId(String name, Integer productId);
    List<Sku> selectAll();
    int updateByPrimaryKey(Sku row);

    int insertOrUpdate(Sku row);

    List<Sku> selectByProductId(Integer productId);
    List<Sku> selectByIds(List<Integer> ids);

}