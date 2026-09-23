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

/**
 * Implementation of {@link SkuTierPriceService} for managing SKU tier prices.
 * Uses MyBatis {@link SkuTierPriceMapper} and batch operations for efficient database access.
 */
@RequiredArgsConstructor
@Service("skuTierPriceService")
@Slf4j
public class SkuTierPriceServiceImpl implements SkuTierPriceService {

    private final SkuTierPriceMapper skuTierPriceMapper;
    private final SqlSessionFactory sqlSessionFactory;

    /**
     * {@inheritDoc}
     * <p>
     * Inserts a single SKU tier price or updates the price when a row for the
     * same {@code skuId} and {@code tierId} already exists.
     */
    @Override
    public int create(SkuTierPriceDto skuTierPriceDto) {
        if (skuTierPriceDto == null) {
            return 0;
        }
        SkuTierPrice skuTierPrice = skuTierPriceDto.toSkuTierPrice();
        long now = System.currentTimeMillis();
        skuTierPrice.setCreatedAt(new Date(now));
        skuTierPrice.setUpdatedAt(new Date(now));
        return skuTierPriceMapper.insertOrUpdate(skuTierPrice);
    }

    /**
     * {@inheritDoc}
     *
     * @param skuIds list of SKU IDs to query
     * @param tierId tier price ID to filter by
     * @return list of SKU tier prices matching the SKU IDs and tier ID
     */
    @Override
    public List<SkuTierPrice> selectBySkusTierId(List<Integer> skuIds, Integer tierId) {
        return skuTierPriceMapper.selectBySkusTierId(skuIds, tierId);
    }

    /**
     * {@inheritDoc}
     *
     * Performs batch insert or update of SKU tier prices for improved performance.
     * Commits the SQL session after operation.
     *
     * @param skuTierPrices list of SKU tier prices to insert or update
     */
    @Override
    public void insetOrUpdateBulk(List<SkuTierPrice> skuTierPrices) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            SkuTierPriceMapper mapper = sqlSession.getMapper(SkuTierPriceMapper.class);
            skuTierPrices.forEach(mapper::insertOrUpdate);
            sqlSession.commit();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Deletes all SKU tier prices associated with the given SKU ID.
     *
     * @param skuId SKU ID whose tier prices will be deleted
     * @return number of deleted records
     */
    @Override
    public int deleteBySkuId(Integer skuId) {
        return skuTierPriceMapper.deleteBySkuId(skuId);
    }

    /**
     * {@inheritDoc}
     *
     * Deletes SKU tier prices for the provided list of SKUs, marking them as deleted.
     * If the SKU list is empty or null, returns 0 immediately.
     *
     * @param skus list of SKUs to delete tier prices for
     * @param deleted flag indicating deletion status to set
     * @param deletedAt timestamp for when the deletion occurred
     * @return number of deleted records
     */
    @Override
    public int deleteBySkuIds(List<Sku> skus, Boolean deleted, Date deletedAt) {
        if (ObjectUtils.isEmpty(skus)) {
            return 0;
        }
        return skuTierPriceMapper.deleteBySkuIds(skus, deleted, deletedAt);
    }
}
