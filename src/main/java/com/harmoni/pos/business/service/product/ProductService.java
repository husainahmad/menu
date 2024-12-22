package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;

import java.util.List;

public interface ProductService {

    Product create(ProductAddDto productDto);
    List<Product> selectByCategory(Integer categoryId);
    List<Product> selectByCategoryBrand(Integer categoryId, Integer brandId);
    Product get(Integer id);
    void selectByNameCategoryId(Integer id, String name, Integer categoryId);
    void updateProductSku(Integer productId, ProductSkuDto productSkuDto);
    Product update(ProductEditDto productEditDto);
    int delete(Integer id);
}
