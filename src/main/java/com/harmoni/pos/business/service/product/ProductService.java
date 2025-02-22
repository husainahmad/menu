package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Product create(ProductAddDto productDto);
    List<Product> selectByCategoryPrice(String authHeader, Integer categoryId);
    List<Product> selectByCategory(Integer categoryId);
    Map<String, Object> selectByCategoryBrand(Integer categoryId, Integer brandId, int page, int size, String search);
    Product get(Integer id);
    void selectByNameCategoryId(Integer id, String name, Integer categoryId);
    void updateProductSku(Integer productId, ProductSkuDto productSkuDto);
    Product update(ProductEditDto productEditDto);
    int delete(Integer id);
}
