package com.harmoni.pos.business.service.customization;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.customizationoption.CustomizationOptionService;
import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.mapper.CustomizationMapper;
import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomizationService} using MyBatis.
 * Provides business logic for managing customizations.
 */
@RequiredArgsConstructor
@Service("customizationService")
@Slf4j
public class CustomizationServiceImpl implements CustomizationService {

    private final CustomizationMapper customizationMapper;
    private final UserService userService;
    private final CustomizationOptionService customizationOptionService;
    private final CustomizationOptionTierPriceService customizationOptionTierPriceService;

    /**
     * Retrieves a paginated list of customizations for the brand associated with the authenticated user.
     *
     * @param authToken the authentication token
     * @param page      the page number
     * @param size      the page size
     * @return a map containing pagination metadata and the list of customizations
     */
    @Override
    public Map<String, Object> listPaginated(String username, int page, int size) {
        User user = userService.selectByUsername(username);
        PaginationUtils.applyPagination(page, size);
        Map<String, Object> paginationData = new HashMap<>();
        List<Customization> customizations = getCustomizationsByBrandId(user.getStore().getChain().getBrandId());
        PageInfo<Customization> categoryPageInfo = new PageInfo<>(customizations);

        categoryPageInfo.setList(populateCustomizationOptions(customizations));

        paginationData.put("page", categoryPageInfo.getPages());
        paginationData.put("size", categoryPageInfo.getSize());
        paginationData.put("total", categoryPageInfo.getTotal());
        paginationData.put("data", categoryPageInfo.getList());
        paginationData.put("navigate", categoryPageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * Batch-fetches options for the given customizations and attaches them.
     *
     * @param customizations list of customizations
     * @return list of customizations with options populated
     */
    private List<Customization> populateCustomizationOptions(List<Customization> customizations) {
        if (customizations == null || customizations.isEmpty()) {
            return customizations;
        }

        List<Integer> customizationIds = customizations.stream()
                .map(Customization::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (customizationIds.isEmpty()) {
            return customizations;
        }

        List<CustomizationOption> options = customizationOptionService.getByCustomizationIds(customizationIds);
        Map<Integer, List<CustomizationOption>> optionsByCustomizationId = options.stream()
                .collect(Collectors.groupingBy(CustomizationOption::getCustomizationId));

        List<Integer> optionIds = options.stream()
                .map(CustomizationOption::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (!optionIds.isEmpty()) {
            List<CustomizationOptionTierPrice> tierPrices =
                    customizationOptionTierPriceService.selectByOptionIds(optionIds);
            Map<Integer, List<CustomizationOptionTierPrice>> tierPricesByOptionId = tierPrices.stream()
                    .collect(Collectors.groupingBy(CustomizationOptionTierPrice::getCustomizationOptionId));
            options.forEach(option -> option.setTierPrices(
                    tierPricesByOptionId.getOrDefault(option.getId(), Collections.emptyList())));
        }

        customizations.forEach(customization -> customization.setCustomizationOptions(
                optionsByCustomizationId.getOrDefault(customization.getId(), Collections.emptyList())));

        return customizations;
    }

    /**
     * Retrieves a customization by its ID.
     *
     * @param id the ID of the customization
     * @return an Optional containing the Customization if found, or empty if not found
     */
    @Override
    public Optional<Customization> getCustomizationById(Integer id) {
        Customization customization = customizationMapper.selectByPrimaryKey(id);
        if (customization == null) {
            return Optional.empty();
        }
        List<Customization> populated = populateCustomizationOptions(List.of(customization));
        return Optional.of(populated == null || populated.isEmpty() ? customization : populated.get(0));
    }

    /**
     * Retrieves customizations by brand ID.
     *
     * @param brandId the brand ID
     * @return a list of Customization entities for the specified brand
     */
    @Override
    public List<Customization> getCustomizationsByBrandId(Integer brandId) {
        return customizationMapper.selectByBrandId(brandId);
    }

    /**
     * Creates a new customization.
     *
     * @param customization the Customization entity to create
     * @return int if creation was successful, 0 otherwise
     */
    @Override
    public int createCustomization(String username, Customization customization) {
        User user = userService.selectByUsername(username);
        customization.setBrandId(user.getStore().getChain().getBrandId());
        customizationMapper.insert(customization);
        return customizationOptionService.createBulk(customization.getCustomizationOptions(), customization.getId());
    }

    /**
     * Updates an existing customization and its options (including tier-based prices).
     *
     * @param customization the Customization entity to update
     * @return int if update was successful, 0 otherwise
     */
    @Override
    public int updateCustomization(Customization customization) {
        customization.setUpdatedAt(new Date(System.currentTimeMillis()));
        int rows = customizationMapper.updateByPrimaryKey(customization);
        if (customization.getCustomizationOptions() != null) {
            customizationOptionService.createBulk(customization.getCustomizationOptions(), customization.getId());
        }
        return rows;
    }

    /**
     * Deletes a customization by its ID.
     *
     * @param id the ID of the customization to delete
     * @return int if deletion was successful, 0 otherwise
     */
    @Override
    public int deleteCustomization(Integer id) {
        return customizationMapper.deleteByPrimaryKey(id) ;
    }
}
