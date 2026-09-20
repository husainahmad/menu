package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationMapper;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.CustomizationOptionTierPriceMapper;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.ProductCustomization;
import com.harmoni.pos.menu.model.SelectionType;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationConfigDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ProductCustomizationService}.
 * Manages the assignment of customization groups to products via the
 * {@code product_customizations} join table, including per-product overrides
 * (required / min / max selection) and ordering.
 */
@Service("productCustomizationService")
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationServiceImpl implements ProductCustomizationService {

    private final ProductCustomizationMapper mapper;
    private final CustomizationMapper customizationMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final CustomizationOptionTierPriceMapper customizationOptionTierPriceMapper;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    @Override
    public Optional<ProductCustomization> getById(Integer id) {
        return Optional.ofNullable(mapper.selectByPrimaryKey(id));
    }

    @Override
    public List<ProductCustomization> getByProductId(Integer productId) {
        return mapper.selectByProductId(productId);
    }

    @Override
    public List<ProductCustomization> getByProductIds(List<Integer> productIds) {
        if (ObjectUtils.isEmpty(productIds)) {
            return List.of();
        }
        return mapper.selectByProductIds(productIds);
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

    @Override
    public int replaceByProductId(List<Integer> customizationIds, Integer productId) {
        mapper.deleteByProductId(productId);

        if (ObjectUtils.isEmpty(customizationIds)) {
            return 0;
        }

        List<ProductCustomization> links = new ArrayList<>();
        Date now = new Date(System.currentTimeMillis());
        List<Integer> distinctIds = customizationIds.stream()
                .filter(id -> !ObjectUtils.isEmpty(id))
                .distinct()
                .collect(Collectors.toList());

        for (int i = 0; i < distinctIds.size(); i++) {
            Integer customizationId = distinctIds.get(i);
            links.add(new ProductCustomization()
                    .setProductId(productId)
                    .setCustomizationId(customizationId)
                    .setSortOrder(i)
                    .setIsActive(true)
                    .setIsDeleted(false)
                    .setCreatedAt(now)
                    .setUpdatedAt(now));
        }

        if (links.isEmpty()) {
            return 0;
        }
        return mapper.insertBulk(links);
    }

    @Override
    public int replaceForProduct(Integer productId, List<Integer> customizationIds) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new BusinessBadRequestException("exception.product.id.badRequest.notFound", null);
        }
        List<Integer> distinctIds = ObjectUtils.isEmpty(customizationIds)
                ? List.of()
                : customizationIds.stream()
                        .filter(id -> !ObjectUtils.isEmpty(id))
                        .distinct()
                        .collect(Collectors.toList());
        validateCustomizations(distinctIds, product.getCategoryId());
        return replaceByProductId(distinctIds, productId);
    }

    /**
     * Validates that all customization IDs exist and belong to the same brand as the product's category.
     */
    private void validateCustomizations(List<Integer> customizationIds, Integer categoryId) {
        if (ObjectUtils.isEmpty(customizationIds)) {
            return;
        }

        List<Customization> customizations = customizationMapper.selectByIds(customizationIds);
        Set<Integer> foundIds = customizations.stream()
                .map(Customization::getId)
                .collect(Collectors.toSet());
        if (foundIds.size() != customizationIds.size()) {
            throw new BusinessBadRequestException("exception.customization.id.badRequest.notFound", null);
        }

        Integer brandId = categoryService.get(categoryId).getBrandId();
        boolean brandMismatch = customizations.stream()
                .anyMatch(customization -> !brandId.equals(customization.getBrandId()));
        if (brandMismatch) {
            throw new BusinessBadRequestException("exception.customization.brand.badRequest.mismatch", null);
        }
    }

    @Override
    public List<ProductCustomizationResponseDto> getDetailedByProductId(Integer productId) {
        List<ProductCustomization> links = mapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(links)) {
            return List.of();
        }

        List<Integer> customizationIds = links.stream()
                .map(ProductCustomization::getCustomizationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Customization> customizationsById = ObjectUtils.isEmpty(customizationIds)
                ? Collections.emptyMap()
                : customizationMapper.selectByIds(customizationIds).stream()
                        .collect(Collectors.toMap(Customization::getId, c -> c));

        if (!customizationsById.isEmpty()) {
            populateOptionsAndTierPrices(customizationsById);
        }

        List<ProductCustomizationResponseDto> response = new ArrayList<>();
        for (ProductCustomization link : links) {
            Customization customization = customizationsById.get(link.getCustomizationId());
            if (customization == null) {
                continue;
            }
            response.add(toResponseDto(link, customization));
        }
        return response;
    }

    /**
     * Attaches options (with their tier prices) to the given customizations.
     */
    private void populateOptionsAndTierPrices(Map<Integer, Customization> customizationsById) {
        List<CustomizationOption> options = customizationOptionMapper.selectByCustomizationIds(
                new ArrayList<>(customizationsById.keySet()));

        List<Integer> optionIds = options.stream()
                .map(CustomizationOption::getId)
                .filter(id -> !ObjectUtils.isEmpty(id))
                .distinct()
                .collect(Collectors.toList());

        if (!optionIds.isEmpty()) {
            List<CustomizationOptionTierPrice> tierPrices =
                    customizationOptionTierPriceMapper.selectByOptionIds(optionIds);
            Map<Integer, List<CustomizationOptionTierPrice>> tierPricesByOptionId = tierPrices.stream()
                    .collect(Collectors.groupingBy(CustomizationOptionTierPrice::getCustomizationOptionId));
            options.forEach(option -> option.setTierPrices(
                    tierPricesByOptionId.getOrDefault(option.getId(), Collections.emptyList())));
        }

        Map<Integer, List<CustomizationOption>> optionsByCustomizationId = options.stream()
                .collect(Collectors.groupingBy(CustomizationOption::getCustomizationId));
        customizationsById.values().forEach(customization ->
                customization.setCustomizationOptions(
                        optionsByCustomizationId.getOrDefault(customization.getId(), Collections.emptyList())));
    }

    private static ProductCustomizationResponseDto toResponseDto(ProductCustomization link,
                                                                 Customization customization) {
        ProductCustomizationResponseDto dto = new ProductCustomizationResponseDto();
        dto.setId(link.getId());
        dto.setProductId(link.getProductId());
        dto.setCustomizationId(customization.getId());
        dto.setName(customization.getName());
        dto.setDescription(customization.getDescription());
        dto.setSelectionType(customization.getSelectionType());
        dto.setBrandId(customization.getBrandId());
        dto.setRequired(effectiveRequired(link, customization));
        dto.setMinSelection(effectiveMin(link, customization));
        dto.setMaxSelection(effectiveMax(link, customization));
        dto.setRequiredOverride(link.getRequiredOverride());
        dto.setMinSelectionOverride(link.getMinSelectionOverride());
        dto.setMaxSelectionOverride(link.getMaxSelectionOverride());
        dto.setSortOrder(link.getSortOrder());
        dto.setOptions(customization.getCustomizationOptions());
        return dto;
    }

    @Override
    public int updateConfiguration(Integer linkId, ProductCustomizationConfigDto config) {
        ProductCustomization link = mapper.selectByPrimaryKey(linkId);
        if (link == null) {
            throw new BusinessBadRequestException("exception.productCustomization.badRequest.notFound", null);
        }

        Customization customization = customizationMapper.selectByPrimaryKey(link.getCustomizationId());
        if (customization == null) {
            throw new BusinessBadRequestException("exception.customization.id.badRequest.notFound", null);
        }

        Integer min = config.getMinSelectionOverride() == null
                ? customization.getMinimumSelection() : config.getMinSelectionOverride();
        Integer max = config.getMaxSelectionOverride() == null
                ? customization.getMaximumSelection() : config.getMaxSelectionOverride();
        Boolean required = config.getRequiredOverride() == null
                ? customization.getRequired() : config.getRequiredOverride();

        List<CustomizationOption> options = customizationOptionMapper.selectByCustomizationId(customization.getId());
        int optionCount = options == null ? 0 : options.size();

        validateConfiguration(customization, min, max, required, optionCount);

        link.setRequiredOverride(config.getRequiredOverride());
        link.setMinSelectionOverride(config.getMinSelectionOverride());
        link.setMaxSelectionOverride(config.getMaxSelectionOverride());
        if (config.getSortOrder() != null) {
            link.setSortOrder(config.getSortOrder());
        }
        link.setUpdatedAt(new Date(System.currentTimeMillis()));
        return mapper.updateConfigurationById(link);
    }

    /**
     * Validates the effective selection configuration for a product-customization link.
     */
    private static void validateConfiguration(Customization customization, Integer min, Integer max,
                                              Boolean required, int optionCount) {
        if (min != null && min < 0) {
            throw new BusinessBadRequestException(
                    "exception.productCustomization.config.badRequest.invalidRange", null);
        }
        if (min != null && max != null && max < min) {
            throw new BusinessBadRequestException(
                    "exception.productCustomization.config.badRequest.invalidRange", null);
        }
        if (SelectionType.SINGLE.equals(customization.getSelectionType()) && max != null && max != 1) {
            throw new BusinessBadRequestException(
                    "exception.productCustomization.config.badRequest.singleOnlyOne", null);
        }
        if (Boolean.TRUE.equals(required) && (min == null || min < 1)) {
            throw new BusinessBadRequestException(
                    "exception.productCustomization.config.badRequest.requiredMin", null);
        }
        if (max != null && max > optionCount) {
            throw new BusinessBadRequestException(
                    "exception.productCustomization.config.badRequest.maxExceeds", null);
        }
    }

    /**
     * Returns the effective required flag (override wins over the master value).
     */
    private static Boolean effectiveRequired(ProductCustomization link, Customization customization) {
        return link.getRequiredOverride() != null ? link.getRequiredOverride() : customization.getRequired();
    }

    /**
     * Returns the effective minimum selection (override wins over the master value).
     */
    private static Integer effectiveMin(ProductCustomization link, Customization customization) {
        return link.getMinSelectionOverride() != null
                ? link.getMinSelectionOverride() : customization.getMinimumSelection();
    }

    /**
     * Returns the effective maximum selection (override wins over the master value).
     */
    private static Integer effectiveMax(ProductCustomization link, Customization customization) {
        return link.getMaxSelectionOverride() != null
                ? link.getMaxSelectionOverride() : customization.getMaximumSelection();
    }
}