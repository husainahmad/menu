package com.harmoni.pos.business.service.skutierprice;

import com.harmoni.pos.business.service.sku.SkuServiceImpl;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.mapper.SkuTierPriceMapper;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("skuTierPriceService")
public class SkuTierPriceServiceImpl implements SkuTierPriceService {

    private final Logger log = LoggerFactory.getLogger(SkuTierPriceServiceImpl.class);
    private final SkuTierPriceMapper skuTierPriceMapper;

    @Override
    public int create(SkuTierPriceDto skuTierPriceDto) {
        return 0;
    }

    @Override
    public List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds) {
        return skuTierPriceMapper.selectBySkusTierId(skuIds);
    }
}
