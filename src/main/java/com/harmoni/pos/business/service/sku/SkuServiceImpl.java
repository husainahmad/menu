package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("skuService")
public class SkuServiceImpl implements SkuService {
    private final Logger log = LoggerFactory.getLogger(SkuServiceImpl.class);
    private final SkuMapper skuMapper;

    @Override
    public int create(SkuDto skuDto) {

        if (!ObjectUtils.isEmpty(skuMapper.selectByNameProductId(skuDto.getName(),
                skuDto.getProductId()))) {
            throw new BusinessBadRequestException("exception.sku.badRequest.duplicate", null);
        }

        int record = skuMapper.insert(skuDto.toSku());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public List<Sku> selectAll() {
        return skuMapper.selectAll();
    }

    @Override
    public List<Sku> selectByProductId(Long productId) {
        return skuMapper.selectByProductId(productId.intValue());
    }
}
