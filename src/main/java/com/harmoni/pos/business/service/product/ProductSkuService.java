package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;

/**
 * Service interface for handling product and SKU creation and updates.
 * <p>
 * Provides operations for creating new products and updating existing ones
 * along with their SKU-related data.
 * </p>
 */
public interface ProductSkuService {

    /**
     * Creates a new product along with its associated SKUs and pricing information.
     *
     * @param productDto the DTO containing product and SKU details
     * @return the newly created {@link Product}
     */
    Product create(ProductAddDto productDto);

    /**
     * Updates an existing product and its associated SKUs.
     *
     * @param productEditDto the DTO containing updated product and SKU details
     * @return the updated {@link Product}
     */
    Product update(ProductEditDto productEditDto);

}
