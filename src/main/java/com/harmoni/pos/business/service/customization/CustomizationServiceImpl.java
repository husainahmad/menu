package com.harmoni.pos.business.service.customization;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.customizationoption.CustomizationOptionService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.mapper.CustomizationMapper;
import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Retrieves a paginated list of customizations for the brand associated with the authenticated user.
     *
     * @param authToken the authentication token
     * @param page      the page number
     * @param size      the page size
     * @return a map containing pagination metadata and the list of customizations
     */
    @Override
    public Map<String, Object> listPaginated(String authToken, int page, int size) {
        PaginationUtils.applyPagination(page, size);
        User user = userService.selectByAuthToken(authToken.substring(7));
        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<Customization> categoryPageInfo = new PageInfo<>(getCustomizationsByBrandId(user.getStore().getChain().getBrandId()));

        paginationData.put("page", categoryPageInfo.getPages());
        paginationData.put("size", categoryPageInfo.getSize());
        paginationData.put("total", categoryPageInfo.getTotal());
        paginationData.put("data", categoryPageInfo.getList());
        paginationData.put("navigate", categoryPageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * Retrieves a customization by its ID.
     *
     * @param id the ID of the customization
     * @return an Optional containing the Customization if found, or empty if not found
     */
    @Override
    public Optional<Customization> getCustomizationById(Integer id) {
        return Optional.ofNullable(customizationMapper.selectByPrimaryKey(id));
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
    public int createCustomization(String authHeader, Customization customization) {
        User user = userService.selectByAuthToken(authHeader.substring(7));
        customization.setBrandId(user.getStore().getChain().getBrandId());
        customizationMapper.insert(customization);
        return customizationOptionService.createBulk(customization.getCustomizationOptions(), customization.getId());
    }

    /**
     * Updates an existing customization.
     *
     * @param customization the Customization entity to update
     * @return int if update was successful, 0 otherwise
     */
    @Override
    public int updateCustomization(Customization customization) {
        return customizationMapper.updateByPrimaryKey(customization);
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
