package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.ProductCustomization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MyBatis mapper for {@link ProductCustomization}.
 */
@Mapper
public interface ProductCustomizationMapper {

    int insert(ProductCustomization productCustomization);

    int updateByPrimaryKey(ProductCustomization productCustomization);

    int deleteByPrimaryKey(Integer id);

    ProductCustomization selectByPrimaryKey(Integer id);

    List<ProductCustomization> selectByProductId(Integer productId);
}
