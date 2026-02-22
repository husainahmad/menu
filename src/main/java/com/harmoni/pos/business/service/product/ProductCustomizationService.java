package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.model.ProductCustomization;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link ProductCustomization} entities.
 */
public interface ProductCustomizationService {

    Optional<ProductCustomization> getById(Integer id);

    List<ProductCustomization> getByProductId(Integer productId);

    int create(ProductCustomization customization);

    int update(ProductCustomization customization);

    int delete(Integer id);
}
