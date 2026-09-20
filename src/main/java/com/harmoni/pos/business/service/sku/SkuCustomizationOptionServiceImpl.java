package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.mapper.SkuCustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.ProductCustomization;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuCustomizationOption;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SkuCustomizationOptionService}.
 * Enforces that any SKU ↔ customization option link references an option
 * belonging to a customization assigned to the SKU's product.
 */
@Service
public class SkuCustomizationOptionServiceImpl implements SkuCustomizationOptionService {

    private final SkuCustomizationOptionMapper mapper;
    private final SkuMapper skuMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final ProductCustomizationMapper productCustomizationMapper;

    public SkuCustomizationOptionServiceImpl(
            SkuCustomizationOptionMapper mapper,
            SkuMapper skuMapper,
            CustomizationOptionMapper customizationOptionMapper,
            ProductCustomizationMapper productCustomizationMapper) {
        this.mapper = mapper;
        this.skuMapper = skuMapper;
        this.customizationOptionMapper = customizationOptionMapper;
        this.productCustomizationMapper = productCustomizationMapper;
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
        validateOptionBelongsToProduct(option);
        return mapper.insert(option);
    }

    @Override
    public int update(SkuCustomizationOption option) {
        validateOptionBelongsToProduct(option);
        return mapper.updateByPrimaryKey(option);
    }

    @Override
    public int delete(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

/**
     * Validates that the customization option being linked to a SKU belongs to
     * a customization group that is assigned (via {@code product_customizations})
     * to the SKU's product.
     *
     * @param option the SKU customization option to validate
     * @throws BusinessBadRequestException if the SKU or option doesn't exist,
     *         or the option doesn't belong to a product-assigned customization
     */
    private void validateOptionBelongsToProduct(SkuCustomizationOption option) {
        if (ObjectUtils.isEmpty(option.getSkuId()) || ObjectUtils.isEmpty(option.getCustomizationOptionId())) {
            throw new BusinessBadRequestException(
                    "exception.skuCustomizationOption.badRequest.missingFields", null);
        }

        Sku sku = skuMapper.selectById(option.getSkuId());
        if (ObjectUtils.isEmpty(sku)) {
            throw new BusinessBadRequestException("exception.sku.badRequest.notFound", null);
        }

        CustomizationOption customizationOption = customizationOptionMapper
                .selectByPrimaryKey(option.getCustomizationOptionId());
        if (ObjectUtils.isEmpty(customizationOption)) {
            throw new BusinessBadRequestException("exception.customizationOption.badRequest.notFound", null);
        }

        List<ProductCustomization> links = productCustomizationMapper.selectByProductId(sku.getProductId());
        boolean belongs = links.stream()
                .anyMatch(link -> customizationOption.getCustomizationId()
                        .equals(link.getCustomizationId()));
        if (!belongs) {
            throw new BusinessBadRequestException(
                    "exception.skuCustomizationOption.badRequest.notInProduct", null);
        }
    }
}
