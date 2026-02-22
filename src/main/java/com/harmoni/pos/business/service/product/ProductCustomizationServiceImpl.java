package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.model.ProductCustomization;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ProductCustomizationService}.
 */
@Service
public class ProductCustomizationServiceImpl implements ProductCustomizationService {

    private final ProductCustomizationMapper mapper;

    public ProductCustomizationServiceImpl(ProductCustomizationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductCustomization> getById(Integer id) {
        return Optional.ofNullable(mapper.selectByPrimaryKey(id));
    }

    @Override
    public List<ProductCustomization> getByProductId(Integer productId) {
        return mapper.selectByProductId(productId);
    }

    @Override
    public int create(ProductCustomization customization) {
        return mapper.insert(customization);
    }

    @Override
    public int update(ProductCustomization customization) {
        return mapper.updateByPrimaryKey(customization);
    }

    @Override
    public int delete(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }
}
