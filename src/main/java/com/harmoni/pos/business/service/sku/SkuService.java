package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;

import java.util.List;

public interface SkuService {

    int create(SkuDto skuDto);
    List<Sku> createOrUpdate(List<Sku> skus);
    List<Sku> selectByProductId(Integer productId);
    List<Sku> selectByIds(List<Integer> ids);
    List<Sku> compareListSkus(List<Sku> skus, List<Integer> ids);
    void updateBulk(List<Sku> skus);
    void validateSkuName(List<Sku> originalSkus, List<Sku> skuDtos);
    void deleteSku(Integer skuId);
    void deleteSkuByProductId(Integer id);
    int insertOrUpdate(Sku sku);
    List<Sku> setSkuIdInListSkus(List<Sku> skus, List<Sku> skusFromDB);
}
