package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;

public interface ProductSkuService {
    Product create(ProductDto productDto);
}
