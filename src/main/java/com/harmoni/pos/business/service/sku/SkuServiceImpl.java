package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.SkuDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service("skuService")
@Slf4j
public class SkuServiceImpl implements SkuService {
    private final SkuMapper skuMapper;
    private final SqlSessionFactory sqlSessionFactory;
    private final SkuTierPriceService skuTierPriceService;
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
    public List<Sku> selectByProductId(Integer productId) {
        return skuMapper.selectByProductId(productId);
    }

    @Override
    public List<Sku> selectByIds(List<Integer> ids) {
        return this.skuMapper.selectByIds(ids);
    }

    @Override
    public List<Sku> compareListSkus(List<Sku> skus, List<Integer> ids) {

        List<Sku> skusByIdes = this.selectByIds(ids);
        AtomicInteger record = new AtomicInteger();
        List<Boolean> skusFound = new ArrayList<>(skus.size());

        skus.forEach(skuPayload -> {
            if (!ObjectUtils.isEmpty(skuPayload.getId())) {
                skusByIdes.forEach(sku -> {
                    if (skuPayload.getId().equals(sku.getId())) {
                        skusFound.add(record.get(), true);
                    }
                });
            }

            record.getAndIncrement();
        });

        skusFound.forEach(aBoolean -> {
            if (!aBoolean) {
                throw new BusinessNoContentRequestException("exception.noContent", null);
            }
        });
        return skusByIdes;
    }

    @Override
    public void updateBulk(List<Sku> skus) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            SkuMapper mapper = sqlSession.getMapper(SkuMapper.class);
            skus.forEach(mapper::insertOrUpdate);
            sqlSession.commit();
        }
    }

    @Override
    public void validateSkuName(List<Sku> originalSkus, List<Sku> skuDtos) {
        skuDtos.forEach(sku -> {
            skuDtos.forEach(skuDto -> {
                if (!ObjectUtils.isEmpty(sku.getId()) && !sku.getId().equals(skuDto.getId())) {
                    if (sku.getProductId().equals(skuDto.getProductId()) &&
                         sku.getName().equals(skuDto.getName())) {
                            throw new BusinessNoContentRequestException("exception.noContent", null);
                    }
                }
            });
        });
    }

    @Override
    public void deleteSku(Integer skuId) {
        Sku sku = skuMapper.selectById(skuId);
        if (ObjectUtils.isEmpty(sku)) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }
        skuTierPriceService.deleteBySkuId(skuId);
        skuMapper.deleteById(skuId);
    }

    @Override
    public List<Sku> setSkuIdInListSkus(List<Sku> skus, List<Sku> skusFromDB) {
        skusFromDB.forEach(sku -> {
            skus.forEach(s -> {
                if (sku.getName().equals(s.getName())) {
                    s.setId(sku.getId());
                }
            });
        });
        return skus;
    }

}
