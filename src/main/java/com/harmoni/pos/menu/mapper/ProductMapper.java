package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Product row);
    Product selectByPrimaryKey(Integer id);
    Product selectByNameCategoryId(String name, Integer categoryId);
    List<Product> selectAll();

    List<Product> selectByCategoryId(Integer categoryId);
    List<Product> selectByCategoryIdBrandId(Integer categoryId, Integer brandId);
    int updateByPrimaryKey(Product row);

}