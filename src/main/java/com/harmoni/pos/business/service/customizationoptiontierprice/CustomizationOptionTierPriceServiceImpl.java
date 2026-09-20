package com.harmoni.pos.business.service.customizationoptiontierprice;

import com.harmoni.pos.menu.mapper.CustomizationOptionTierPriceMapper;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Implementation of {@link CustomizationOptionTierPriceService} for managing customization option tier prices.
 * Uses MyBatis {@link CustomizationOptionTierPriceMapper} and batch operations for efficient database access.
 */
@RequiredArgsConstructor
@Service("customizationOptionTierPriceService")
@Slf4j
public class CustomizationOptionTierPriceServiceImpl implements CustomizationOptionTierPriceService {

    private final CustomizationOptionTierPriceMapper customizationOptionTierPriceMapper;
    private final SqlSessionFactory sqlSessionFactory;

    /**
     * {@inheritDoc}
     *
     * @param optionIds list of customization option IDs
     * @return list of customization option tier prices
     */
    @Override
    public List<CustomizationOptionTierPrice> selectByOptionIds(List<Integer> optionIds) {
        if (ObjectUtils.isEmpty(optionIds)) {
            return List.of();
        }
        return customizationOptionTierPriceMapper.selectByOptionIds(optionIds);
    }

    /**
     * {@inheritDoc}
     *
     * @param optionIds list of customization option IDs
     * @param tierId the Tier ID
     * @return list of customization option tier prices
     */
    @Override
    public List<CustomizationOptionTierPrice> selectByOptionIdsAndTierId(List<Integer> optionIds, Integer tierId) {
        if (ObjectUtils.isEmpty(optionIds)) {
            return List.of();
        }
        return customizationOptionTierPriceMapper.selectByOptionIdsAndTierId(optionIds, tierId);
    }

    /**
     * {@inheritDoc}
     *
     * Performs batch insert or update of customization option tier prices for improved performance.
     * Commits the SQL session after operation.
     *
     * @param tierPrices list of customization option tier prices to insert or update
     */
    @Override
    public void insertOrUpdateBulk(List<CustomizationOptionTierPrice> tierPrices) {
        if (ObjectUtils.isEmpty(tierPrices)) {
            return;
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            CustomizationOptionTierPriceMapper mapper = sqlSession.getMapper(CustomizationOptionTierPriceMapper.class);
            tierPrices.forEach(mapper::insertOrUpdate);
            sqlSession.commit();
        }
    }
}