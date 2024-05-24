package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;

import java.util.List;

public interface ProductService {

    int create(ProductDto productDto);
    List<Product> selectByCategory(Integer categoryId);
    List<Product> selectByCategoryBrand(Integer categoryId, Integer brandId);
    Product get(Integer id);
    void selectByNameCategoryId(String name, Integer categoryId);
    void updateProductSku(Integer productId, ProductSkuDto productSkuDto);
}
