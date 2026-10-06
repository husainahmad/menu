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

    /**
     * The option links of several SKUs at once.
     * <p>
     * A caller walking a basket needs every SKU's links; asking one at a time costs a query
     * per line. Soft deleted links are already excluded.
     *
     * @param skuIds the SKUs to look up
     * @return the links for those SKUs, empty when the list is empty
     */
    List<SkuCustomizationOption> getBySkuIds(List<Integer> skuIds);

    int create(SkuCustomizationOption option);

    int update(SkuCustomizationOption option);

    int delete(Integer id);
}
