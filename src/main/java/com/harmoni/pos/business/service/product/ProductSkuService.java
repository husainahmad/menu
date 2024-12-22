package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;

public interface ProductSkuService {
    Product create(ProductAddDto productDto);
    Product update(ProductEditDto productEditDto);

}
