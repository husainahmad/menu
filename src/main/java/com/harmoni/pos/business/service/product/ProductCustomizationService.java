package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.ProductCustomization;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationConfigDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link ProductCustomization} entities.
 */
public interface ProductCustomizationService {

    Optional<ProductCustomization> getById(Integer id);

    List<ProductCustomization> getByProductId(Integer productId);

    List<ProductCustomization> getByProductIds(List<Integer> productIds);

    int create(ProductCustomization customization);

    int update(ProductCustomization customization);

    int delete(Integer id);

    /**
     * Replaces all customization links for a product with the given IDs.
     * The position of each ID in the list determines its {@code sort_order}.
     *
     * @param customizationIds the customization IDs to assign (empty or null clears all links)
     * @param productId        the product ID
     * @return number of rows affected
     */
    int replaceByProductId(List<Integer> customizationIds, Integer productId);

    /**
     * Replaces all customization links for a product with the given IDs (public API path).
     * Validates that the product exists and that every customization exists and belongs to
     * the product's brand. The position of each ID in the list determines its {@code sort_order}.
     *
     * @param productId        the product ID
     * @param customizationIds the customization IDs to assign (empty clears all links)
     * @return number of rows affected
     */
    int replaceForProduct(Integer productId, List<Integer> customizationIds);

    /**
     * Retrieves the customizations assigned to a product with their per-product
     * configuration (effective values, overrides, ordering) and options/tier prices.
     *
     * @param productId the product ID
     * @return ordered list of product-customization details
     */
    List<ProductCustomizationResponseDto> getDetailedByProductId(Integer productId);

    /**
     * Updates the per-product override configuration of a single link.
     *
     * @param linkId the product-customization link ID
     * @param config the override configuration to apply
     * @return number of rows affected
     */
    int updateConfiguration(Integer linkId, ProductCustomizationConfigDto config);
}
