package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.ProductDto;
import com.harmoni.pos.menu.model.dto.SkuDto;

import java.util.List;

public interface SkuService {

    int create(SkuDto skuDto);
    List<Sku> selectAll();

    List<Sku> selectByProductId(Long productId);
}
