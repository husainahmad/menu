package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductImageMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(ProductImage row);
    ProductImage selectByPrimaryKey(Integer id);
    ProductImage selectByProductKey(Integer productId);
    int updateByPrimaryKey(ProductImage productImage);
    int updateImageByProductKey(ProductImage productImage);
    int updateProductIdByPrimaryKey(ProductImage productImage);
}