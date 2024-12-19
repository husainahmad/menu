package com.harmoni.pos.business.service.skutierprice;

import com.harmoni.pos.menu.mapper.SkuTierPriceMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service("skuTierPriceService")
@Slf4j
public class SkuTierPriceServiceImpl implements SkuTierPriceService {

    private final SkuTierPriceMapper skuTierPriceMapper;
    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public int create(SkuTierPriceDto skuTierPriceDto) {
        return 0;
    }

    @Override
    public List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId) {
        return skuTierPriceMapper.selectBySkusTierId(skuIds, tierId);
    }

    @Override
    public void insetOrUpdateBulk(List<SkuTierPrice> skuTierPrices) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            SkuTierPriceMapper mapper = sqlSession.getMapper(SkuTierPriceMapper.class);
            skuTierPrices.forEach(mapper::insertOrUpdate);
            sqlSession.commit();
        }
    }

    @Override
    public int deleteBySkuId(Integer skuId) {
        return skuTierPriceMapper.deleteBySkuId(skuId);
    }

    @Override
    public int deleteBySkuIds(List<Sku> skus, Boolean deleted, Date deletedAt) {
        if (ObjectUtils.isEmpty(skus)) {
            return 0;
        }
        return skuTierPriceMapper.deleteBySkuIds(skus, deleted, deletedAt);
    }
}
