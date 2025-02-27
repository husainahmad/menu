package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProductMapper {

    int deleteByPrimaryKey(Integer id, Boolean deleted, Date deletedAt);
    int insert(Product row);
    Product selectByPrimaryKey(Integer id);
    Product selectByNameCategoryId(String name, Integer categoryId);
    List<Product> selectAll();
    List<Product> selectByIds(List<Integer> ids, Integer brandId);
    List<Product> selectByCategoryId(Integer categoryId);
    List<Product> selectByCategoryIdPrice(Integer categoryId, Integer tierId);
    List<Product> selectByCategoryIdBrandId(Integer categoryId, Integer brandId, String search);
    int updateByPrimaryKey(Product row);

}