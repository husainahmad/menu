package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.menu.model.SkuCustomizationOption;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for {@link SkuCustomizationOption}.
 */
public interface SkuCustomizationOptionService {

    Optional<SkuCustomizationOption> getById(Integer id);

    List<SkuCustomizationOption> getBySkuId(Integer skuId);

    int create(SkuCustomizationOption option);

    int update(SkuCustomizationOption option);

    int delete(Integer id);
}
