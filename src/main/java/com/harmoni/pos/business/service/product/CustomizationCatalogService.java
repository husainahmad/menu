package com.harmoni.pos.business.service.product;

import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductPage;

public interface CustomizationCatalogService {

    CustomizationProductPage selectByCustomization(Integer customizationId, Integer limit, CallerScope scope);
}
