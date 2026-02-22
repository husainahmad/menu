package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.SkuCustomizationOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MyBatis mapper for {@link SkuCustomizationOption}.
 */
@Mapper
public interface SkuCustomizationOptionMapper {

    int insert(SkuCustomizationOption skuCustomizationOption);

    int updateByPrimaryKey(SkuCustomizationOption skuCustomizationOption);

    int deleteByPrimaryKey(Integer id);

    SkuCustomizationOption selectByPrimaryKey(Integer id);

    List<SkuCustomizationOption> selectBySkuId(Integer skuId);
}
