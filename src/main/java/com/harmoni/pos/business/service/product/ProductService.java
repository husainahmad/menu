package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing product-related operations.
 */
public interface ProductService {

    /**
     * Creates a new product using the given DTO.
     *
     * @param productDto the DTO containing product details to be added
     * @return the created product
     */
    Product create(ProductAddDto productDto);

    /**
     * Retrieves products by category ID and user-based pricing context using the JWT token.
     *
     * @param username the username
     * @param categoryId the category ID to filter products
     * @return list of products with contextual pricing
     */
    List<Product> selectByCategoryPrice(String username, Integer categoryId);

    /**
     * Retrieves products by category ID.
     *
     * @param categoryId the category ID
     * @return list of products
     */
    List<Product> selectByCategory(Integer categoryId);

    /**
     * Retrieves paginated and optionally filtered products by category and brand.
     *
     * @param categoryId the category ID
     * @param brandId    the brand ID
     * @param page       the page number (starting from 1)
     * @param size       the page size
     * @param search     optional search keyword for product name or description
     * @return a map containing paginated product data and metadata
     */
    Map<String, Object> selectByCategoryBrand(Integer categoryId, Integer brandId, int page, int size, String search);

    /**
     * Retrieves a product by its unique ID.
     *
     * @param id the product ID
     * @return the product
     */
    Product get(Integer id);

    /**
     * Retrieves a list of products by their IDs and enriches based on the provided JWT token.
     *
     * @param ids      list of product IDs
     * @param jwtToken JWT token for user-specific data enrichment
     * @return list of products
     */
    List<Product> getByList(List<Integer> ids, String username);

    /**
     * Validates if a product name already exists in a category (for uniqueness check).
     *
     * @param id         current product ID (for update context)
     * @param name       name of the product to check
     * @param categoryId the category ID the product belongs to
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if name already exists
     */
    void selectByNameCategoryId(Integer id, String name, Integer categoryId);

    /**
     * Updates the SKU-related details of a product.
     *
     * @param productId     ID of the product to update
     * @param productSkuDto the DTO containing SKU details to be updated
     */
    void updateProductSku(Integer productId, ProductSkuDto productSkuDto);

    /**
     * Updates a product using the provided edit DTO.
     *
     * @param productEditDto the DTO containing updated product details
     * @return the updated product
     */
    Product update(ProductEditDto productEditDto);

    /**
     * Deletes a product by its ID.
     *
     * @param id the product ID
     * @return number of rows affected
     */
    int delete(Integer id);

    /**
     * Search products by product name LIKE (for AI).
     * @param productName the product name keyword
     * @return list of matching products
     */
    List<Product> searchByProductName(String productName);
}
