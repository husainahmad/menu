package com.harmoni.pos.business.service.customizationoption;

import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomizationOptionService}.
 * Provides CRUD operations for customization options belonging to a customization group.
 */
@Service
public class CustomizationOptionServiceImpl implements CustomizationOptionService {

    private final CustomizationOptionMapper optionMapper;
    private final CustomizationOptionTierPriceService tierPriceService;

    public CustomizationOptionServiceImpl(CustomizationOptionMapper optionMapper,
                                          CustomizationOptionTierPriceService tierPriceService) {
        this.optionMapper = optionMapper;
        this.tierPriceService = tierPriceService;
    }

    /**
     * Retrieves a customization option by its ID.
     *
     * @param id the option ID
     * @return an Optional with the option if found, or empty otherwise
     */
    @Override
    public Optional<CustomizationOption> getById(Integer id) {
        return Optional.ofNullable(optionMapper.selectByPrimaryKey(id));
    }

    /**
     * Retrieves all options for a specific customization ID.
     *
     * @param customizationId the parent customization ID
     * @return list of options for the customization
     */
    @Override
    public List<CustomizationOption> getByCustomizationId(Integer customizationId) {
        return optionMapper.selectByCustomizationId(customizationId);
    }

    /**
     * Retrieves all options for a list of customization IDs.
     *
     * @param customizationIds the parent customization IDs
     * @return list of options for the given customizations
     */
    @Override
    public List<CustomizationOption> getByCustomizationIds(List<Integer> customizationIds) {
        if (customizationIds == null || customizationIds.isEmpty()) {
            return List.of();
        }
        return optionMapper.selectByCustomizationIds(customizationIds);
    }

    /**
     * Creates a new customization option.
     *
     * @param option the option to create
     * @return true if created successfully
     */
    @Override
    public boolean create(CustomizationOption option) {
        return optionMapper.insert(option) > 0;
    }

    /**
     * Creates or updates a list of options in bulk, including their tier-based prices.
     *
     * @param options          the list of options to create or update
     * @param customizationId the parent customization ID
     * @return number of rows affected
     */
    @Override
    public int createBulk(List<CustomizationOption> options, Integer customizationId) {
        int rows = optionMapper.insertOrUpdateBulk(options, customizationId);
        persistTierPrices(options, customizationId);
        return rows;
    }

    /**
     * Resolves option IDs (by name) and upserts the tier-based prices for each option's {@code tierPrices}.
     *
     * @param options           the list of options that were created or updated
     * @param customizationId   the parent customization ID
     */
    private void persistTierPrices(List<CustomizationOption> options, Integer customizationId) {
        if (ObjectUtils.isEmpty(options)) {
            return;
        }

        boolean hasTierPrices = options.stream()
                .anyMatch(option -> !ObjectUtils.isEmpty(option.getTierPrices()));
        if (!hasTierPrices) {
            return;
        }

        Map<String, Integer> existingIdsByName = optionMapper.selectByCustomizationId(customizationId).stream()
                .collect(Collectors.toMap(CustomizationOption::getName, CustomizationOption::getId, (a, b) -> a));

        List<CustomizationOptionTierPrice> tierPrices = new ArrayList<>();
        for (CustomizationOption option : options) {
            Integer optionId = option.getId();
            if (optionId == null) {
                optionId = existingIdsByName.get(option.getName());
            }
            if (optionId == null || ObjectUtils.isEmpty(option.getTierPrices())) {
                continue;
            }
            Integer resolvedOptionId = optionId;
            option.getTierPrices().forEach(tierPrice -> tierPrice.setCustomizationOptionId(resolvedOptionId));
            tierPrices.addAll(option.getTierPrices());
        }

        if (!tierPrices.isEmpty()) {
            tierPriceService.insertOrUpdateBulk(tierPrices);
        }
    }

    /**
     * Updates an existing customization option.
     *
     * @param option the option with updated data
     * @return true if updated successfully
     */
    @Override
    public boolean update(CustomizationOption option) {
        return optionMapper.updateByPrimaryKey(option) > 0;
    }

    /**
     * Deletes a customization option by its ID.
     *
     * @param id the option ID
     * @return true if deleted successfully
     */
    @Override
    public boolean delete(Integer id) {
        return optionMapper.deleteByPrimaryKey(id) > 0;
    }
}