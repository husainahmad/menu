package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    int create(ProductDto productDto);
    List<Product> seelctAll();

    Product get(Long id);

}
