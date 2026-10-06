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

    /**
     * The option links for several SKUs at once.
     * <p>
     * Exists so a caller working through a basket can resolve every SKU's permitted options
     * in one query instead of one per SKU.
     *
     * @param skuIds the SKUs to look up
     * @return the links for those SKUs, empty when the list is empty
     */
    List<SkuCustomizationOption> selectBySkuIds(List<Integer> skuIds);
}
