package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.mapper.SkuCustomizationOptionMapper;
import com.harmoni.pos.menu.model.SkuCustomizationOption;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SkuCustomizationOptionService}.
 */
@Service
public class SkuCustomizationOptionServiceImpl implements SkuCustomizationOptionService {

    private final SkuCustomizationOptionMapper mapper;

    public SkuCustomizationOptionServiceImpl(SkuCustomizationOptionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<SkuCustomizationOption> getById(Integer id) {
        return Optional.ofNullable(mapper.selectByPrimaryKey(id));
    }

    @Override
    public List<SkuCustomizationOption> getBySkuId(Integer skuId) {
        return mapper.selectBySkuId(skuId);
    }

    @Override
    public int create(SkuCustomizationOption option) {
        return mapper.insert(option);
    }

    @Override
    public int update(SkuCustomizationOption option) {
        return mapper.updateByPrimaryKey(option);
    }

    @Override
    public int delete(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }
}
