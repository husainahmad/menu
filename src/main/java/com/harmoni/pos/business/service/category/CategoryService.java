package com.harmoni.pos.business.service.category;

import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing Category entities in the POS system.
 *
 * <p>This interface defines business operations such as creation, deletion, retrieval, and listing
 * of product categories, including paginated and filtered access based on user authentication or brand association.</p>
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param categoryDto the DTO containing the category details
     * @return the number of rows inserted
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if a category with the same name already exists
     * @throws com.harmoni.pos.exception.BusinessNoContentRequestException if the insertion fails
     */
    int create(CategoryDto categoryDto);

    /**
     * Deletes an existing category by its ID.
     *
     * @param id the unique identifier of the category
     * @return the number of rows deleted
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the category does not exist
     */
    int delete(Integer id);

    /**
     * Retrieves a list of categories accessible to the authenticated user.
     *
     * @param username the username identifying the user
     * @return a list of categories
     */
    List<Category> getListByUserAuth(String username);

    /**
     * Retrieves a paginated list of categories accessible to the authenticated user.
     *
     * @param username the username identifying the user
     * @param page      the page number (0-based)
     * @param size      the number of records per page
     * @return a map containing paginated category data (e.g., total count, current page items)
     */
    Map<String, Object> listPaginated(String username, int page, int size);

    /**
     * Retrieves a list of categories associated with a specific brand.
     *
     * @param brandId the unique identifier of the brand
     * @return a list of categories under the brand
     */
    List<Category> selectByBrandId(Integer brandId);

    /**
     * Retrieves a single category by its ID.
     *
     * @param id the unique identifier of the category
     * @return the category entity
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the category does not exist
     */
    Category get(Integer id);

    /**
     * Search categories by name LIKE (for AI).
     * @param categoryName the category name keyword
     * @return list of matching categories
     */
    List<Category> searchByCategoryName(String categoryName);
}
