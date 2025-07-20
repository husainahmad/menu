package com.harmoni.pos.business.service.category;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import com.harmoni.pos.menu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CategoryService} providing business logic for managing categories.
 */
@RequiredArgsConstructor
@Service("categoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final UserService userService;
    private final StoreTierService storeTierService;
    private final TierMenuService tierMenuService;

    /**
     * Creates a new category based on the provided DTO.
     *
     * @param categoryDto the category data transfer object
     * @return number of rows inserted
     * @throws BusinessBadRequestException       if a category with the same name already exists in the brand
     * @throws BusinessNoContentRequestException if insertion fails
     */
    @Override
    public int create(CategoryDto categoryDto) {
        if (!ObjectUtils.isEmpty(categoryMapper.selectByNameBrandId(categoryDto.getName(), categoryDto.getBrandId()))) {
            throw new BusinessBadRequestException("exception.category.badRequest.duplicate", null);
        }

        int inserted = categoryMapper.insert(categoryDto.toCategory());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the category ID
     * @return number of rows deleted
     */
    @Override
    public int delete(Integer id) {
        Category category = this.get(id);
        return categoryMapper.deleteByPrimaryKey(category.getId());
    }

    /**
     * Retrieves a list of categories based on the authenticated user's tier.
     *
     * @param authToken the JWT token of the user
     * @return list of accessible categories
     */
    @Override
    public List<Category> getListByUserAuth(String authToken) {
        User user = userService.selectByAuthToken(authToken.substring(7));
        StoreTier storeTier = storeTierService.selectByStoreId(user.getStore().getId());
        List<TierMenu> tierMenus = tierMenuService.getMenusByTierId(storeTier.getTierMenuId());
        return tierMenus.stream()
                .map(TierMenu::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of categories for the brand associated with the authenticated user.
     *
     * @param authToken the JWT token
     * @param page      page number
     * @param size      page size
     * @return paginated map containing category data and metadata
     */
    @Override
    public Map<String, Object> listPaginated(String authToken, int page, int size) {
        PaginationUtils.applyPagination(page, size);
        User user = userService.selectByAuthToken(authToken.substring(7));
        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<Category> categoryPageInfo = new PageInfo<>(this.selectByBrandId(user.getStore().getChain().getBrandId()));

        paginationData.put("page", categoryPageInfo.getPages());
        paginationData.put("size", categoryPageInfo.getSize());
        paginationData.put("total", categoryPageInfo.getTotal());
        paginationData.put("data", categoryPageInfo.getList());
        paginationData.put("navigate", categoryPageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * Retrieves all categories associated with a brand.
     *
     * @param brandId the brand ID
     * @return list of categories under the brand
     */
    @Override
    public List<Category> selectByBrandId(Integer brandId) {
        return categoryMapper.selectByBrandId(brandId);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the category ID
     * @return category object
     * @throws BusinessBadRequestException if category is not found
     */
    @Override
    public Category get(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(category)) {
            throw new BusinessBadRequestException("exception.category.id.badRequest.notFound", null);
        }
        return category;
    }
}
