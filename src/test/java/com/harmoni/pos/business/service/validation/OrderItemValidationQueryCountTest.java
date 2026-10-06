package com.harmoni.pos.business.service.validation;

import com.harmoni.pos.business.service.customization.CustomizationSelectionValidator;
import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.business.service.sku.SkuCustomizationOptionService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuCustomizationOption;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.TierMenu;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import com.harmoni.pos.menu.model.dto.validation.CustomizationGroupSelectionDto;
import com.harmoni.pos.menu.model.dto.validation.CustomizationOptionSelectionDto;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemRequest;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Guards the claim that order validation reads its catalogue state once per request rather
 * than once per basket line.
 * <p>
 * The other test in this package verifies a hand picked list of batch methods. This one
 * counts invocations across <em>every</em> collaborator the service holds, which is the
 * stronger guarantee: a query added later, on a collaborator nobody remembered to check,
 * still fails here.
 * <p>
 * The invariant asserted is that no collaborator method is called more than once, not that
 * the total equals some number. Adding a legitimate batch read for a new table should not
 * fail this test; issuing a read per line must.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderItemValidationQueryCountTest {

    private static final Integer STORE_ID = 1;
    private static final Integer PRICE_TIER_ID = 5;
    private static final Integer MENU_TIER_ID = 4;
    private static final Integer GROUP_ID = 100;
    private static final Integer OPTION_ID = 1001;
    private static final Integer CATEGORY_ID = 3;

    @Mock
    ProductMapper productMapper;
    @Mock
    SkuMapper skuMapper;
    @Mock
    StoreTierService storeTierService;
    @Mock
    TierMenuService tierMenuService;
    @Mock
    SkuTierPriceService skuTierPriceService;
    @Mock
    ProductCustomizationService productCustomizationService;
    @Mock
    SkuCustomizationOptionService skuCustomizationOptionService;
    @Mock
    CustomizationOptionMapper customizationOptionMapper;
    @Mock
    CustomizationOptionTierPriceService customizationOptionTierPriceService;

    @Test
    void noCollaboratorIsCalledMoreThanOnceNoMatterHowManyLinesTheBasketHas() {
        for (boolean priced : new boolean[] {true, false}) {
            for (boolean withChoices : new boolean[] {true, false}) {
                int one = validateBasketOf(1, priced, withChoices);
                for (int lineCount : new int[] {2, 5, 20}) {
                    int many = validateBasketOf(lineCount, priced, withChoices);
                    assertEquals(one, many,
                            "priced=" + priced + " choices=" + withChoices
                                    + ": " + lineCount + " lines must cost the same as 1, was " + many + " vs " + one);
                }
            }
        }
    }

    @Test
    void theLazyPerIdLookupsThisReplacedAreNeverCalled() {
        validateBasketOf(5, true, true);

        verify(productCustomizationService, never()).getDetailedByProductId(any());
        verify(skuCustomizationOptionService, never()).getBySkuId(any());
    }

    /**
     * Builds a basket where every line names its own product and SKU, which is what a real
     * basket mostly looks like and what used to defeat any cache keyed on the last ID.
     *
     * @param lineCount   how many lines to send
     * @param priced      whether the store has a price tier, which selects the tier lookup
     * @param withChoices whether lines carry customization options
     * @return the number of collaborator method invocations the service made
     */
    private int validateBasketOf(int lineCount, boolean priced, boolean withChoices) {
        Object[] collaborators = {productMapper, skuMapper, storeTierService, tierMenuService,
                skuTierPriceService, productCustomizationService, skuCustomizationOptionService,
                customizationOptionMapper, customizationOptionTierPriceService};
        clearInvocations(collaborators);
        givenCatalogueFor(priced, withChoices);

        List<ValidateOrderItemRequest> lines = new ArrayList<>();
        for (int index = 0; index < lineCount; index++) {
            int id = 1000 + index;
            ValidateOrderItemRequest line = new ValidateOrderItemRequest();
            line.setProductId(id);
            line.setSkuId(id);
            line.setQuantity(1);
            if (withChoices) {
                line.setCustomizations(new ArrayList<>(List.of(choice(OPTION_ID, 2))));
            }
            lines.add(line);
        }
        ValidateOrderItemsRequest request = new ValidateOrderItemsRequest();
        request.setStoreId(STORE_ID);
        request.setItems(lines);

        service().validate(request);

        Map<String, Integer> callsPerMethod = new LinkedHashMap<>();
        for (Object collaborator : collaborators) {
            String owner = collaborator.getClass().getSimpleName().replaceAll("\\$MockitoMock\\$.*", "");
            for (Invocation invocation : mockingDetails(collaborator).getInvocations()) {
                String call = owner + "." + invocation.getMethod().getName();
                callsPerMethod.merge(call, 1, Integer::sum);
            }
        }
        assertTrue(callsPerMethod.values().stream().noneMatch(count -> count > 1),
                "a collaborator was called more than once, so something is looping: " + callsPerMethod);
        return callsPerMethod.size();
    }

    private void givenCatalogueFor(boolean priced, boolean withChoices) {
        StoreTier storeTier = new StoreTier().setStoreId(STORE_ID).setTierMenuId(9)
                .setTierMenu(new TierMenu().setId(9).setTierId(MENU_TIER_ID));
        if (priced) {
            storeTier.setTierPriceId(PRICE_TIER_ID);
        }
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(storeTier);
        when(tierMenuService.getMenusByTierId(MENU_TIER_ID))
                .thenReturn(List.of(new TierMenu().setCategoryId(CATEGORY_ID)));

        when(productMapper.selectByIds(anyList(), anyInt())).thenAnswer(invocation -> {
            List<Product> products = new ArrayList<>();
            for (Integer id : invocation.<List<Integer>>getArgument(0)) {
                products.add(new Product().setId(id).setName("Product " + id).setCategoryId(CATEGORY_ID)
                        .setCategory(new Category().setId(CATEGORY_ID).setName("Beverages")));
            }
            return products;
        });
        when(skuMapper.selectByIds(anyList())).thenAnswer(invocation -> {
            List<Sku> skus = new ArrayList<>();
            for (Integer id : invocation.<List<Integer>>getArgument(0)) {
                skus.add(new Sku().setId(id).setProductId(id).setName("Sku " + id)
                        .setActive(true).setDeleted(false));
            }
            return skus;
        });
        when(skuTierPriceService.selectBySkusTierId(anyList(), any())).thenAnswer(invocation -> {
            List<SkuTierPrice> prices = new ArrayList<>();
            for (Integer id : invocation.<List<Integer>>getArgument(0)) {
                prices.add(new SkuTierPrice().setSkuId(id).setTierId(PRICE_TIER_ID)
                        .setPrice(new BigDecimal("20000")));
            }
            return prices;
        });

        // A product carrying a customization whose option is what the basket picks, so the
        // option and option price reads are genuinely exercised.
        Map<Integer, List<ProductCustomizationResponseDto>> groups = new HashMap<>();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenAnswer(invocation -> {
            groups.clear();
            for (Integer id : invocation.<List<Integer>>getArgument(0)) {
                ProductCustomizationResponseDto group = new ProductCustomizationResponseDto();
                group.setCustomizationId(GROUP_ID);
                group.setProductId(id);
                group.setName("Milk");
                group.setRequired(false);
                // The basket deliberately carries a quantity of 2 to exercise the
                // option price reads; the group must permit that.
                group.setAllowQuantity(true);
                group.setMinSelection(null);
                group.setMaxSelection(3);
                group.setOptions(List.of(new CustomizationOption().setId(OPTION_ID)
                        .setCustomizationId(GROUP_ID).setName("Oat").setIsDeleted(false)));
                groups.put(id, List.of(group));
            }
            return groups;
        });
        when(skuCustomizationOptionService.getBySkuIds(anyList())).thenAnswer(invocation -> {
            List<SkuCustomizationOption> links = new ArrayList<>();
            for (Integer id : invocation.<List<Integer>>getArgument(0)) {
                links.add(new SkuCustomizationOption().setSkuId(id).setCustomizationOptionId(OPTION_ID));
            }
            return links;
        });
        when(customizationOptionMapper.selectByIds(anyList())).thenReturn(List.of(
                new CustomizationOption().setId(OPTION_ID).setCustomizationId(GROUP_ID)
                        .setName("Oat").setIsDeleted(false)));
        when(customizationOptionTierPriceService.selectByOptionIdsAndTierId(anyList(), any()))
                .thenReturn(List.of(new CustomizationOptionTierPrice()
                        .setCustomizationOptionId(OPTION_ID).setTierId(PRICE_TIER_ID)
                        .setPrice(new BigDecimal("4000"))));
        when(customizationOptionTierPriceService.selectByOptionIds(anyList()))
                .thenReturn(List.of(new CustomizationOptionTierPrice()
                        .setCustomizationOptionId(OPTION_ID).setTierId(1)
                        .setPrice(new BigDecimal("4000"))));
    }

    private static CustomizationGroupSelectionDto choice(Integer optionId, int quantity) {
        CustomizationOptionSelectionDto option = new CustomizationOptionSelectionDto();
        option.setCustomizationOptionId(optionId);
        option.setQuantity(quantity);
        CustomizationGroupSelectionDto group = new CustomizationGroupSelectionDto();
        group.setCustomizationId(GROUP_ID);
        group.setOptions(new ArrayList<>(List.of(option)));
        return group;
    }

    private OrderItemValidationServiceImpl service() {
        return new OrderItemValidationServiceImpl(productMapper, skuMapper, storeTierService, tierMenuService,
                skuTierPriceService, productCustomizationService, skuCustomizationOptionService,
                customizationOptionMapper, customizationOptionTierPriceService,
                new CustomizationSelectionValidator());
    }
}