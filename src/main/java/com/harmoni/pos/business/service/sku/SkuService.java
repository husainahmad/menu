package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.ProductDto;
import com.harmoni.pos.menu.model.dto.SkuDto;

import java.util.List;

public interface SkuService {

    int create(SkuDto skuDto);
    List<Sku> selectByProductId(Integer productId);
    List<Sku> selectByIds(List<Integer> ids);
    List<Sku> compareListSkus(List<Sku> skus, List<Integer> ids);
    void updateBulk(List<Sku> skus);
    //TODO check if new update SKU name is exist in the same product and category
    void validateSkuName(List<Sku> originalSkus, List<Sku> skuDtos);
}
