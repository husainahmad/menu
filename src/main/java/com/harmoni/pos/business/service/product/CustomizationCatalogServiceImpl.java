package com.harmoni.pos.business.service.product;

import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductDto;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductPage;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductSkuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("customizationCatalogService")
@RequiredArgsConstructor
public class CustomizationCatalogServiceImpl implements CustomizationCatalogService {

    private static final int MAX_LIMIT = 30;

    private final ProductCustomizationMapper productCustomizationMapper;

    @Override
    public CustomizationProductPage selectByCustomization(Integer customizationId, Integer limit, CallerScope scope) {
        if (customizationId == null || customizationId <= 0) {
            throw new BusinessBadRequestException("exception.customization.id.badRequest.invalid", null);
        }

        int size = limit == null || limit <= 0 ? MAX_LIMIT : Math.min(limit, MAX_LIMIT);
        CallerScope effective = scope == null ? CallerScope.global() : scope;
        List<Integer> brandIds = null;
        if (!effective.isGlobal()) {
            brandIds = new ArrayList<>(effective.getBrandIds());
            if (brandIds.isEmpty()) {
                return new CustomizationProductPage();
            }
        }

        List<CustomizationProductDto> products = productCustomizationMapper
                .selectProductsByCustomizationId(customizationId, brandIds, size).stream()
                .filter(product -> effective.isGlobal()
                        || (product.getBrandId() != null && effective.getBrandIds().contains(product.getBrandId())))
                .map(CustomizationCatalogServiceImpl::withoutEmptySkus)
                .toList();
        return new CustomizationProductPage().setProducts(products);
    }

    private static CustomizationProductDto withoutEmptySkus(CustomizationProductDto product) {
        List<CustomizationProductSkuDto> skus = product.getSkus() == null
                ? List.of()
                : product.getSkus().stream().filter(sku -> sku != null && sku.getId() != null).toList();
        return product.setSkus(skus);
    }
}
