package com.harmoni.pos.business.service.skutierprice;

import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;

import java.util.List;

public interface SkuTierPriceService {

    int create(SkuTierPriceDto skuTierPriceDto);
    List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId);
    void insetOrUpdateBulk(List<SkuTierPrice> skuTierPrices);
}
