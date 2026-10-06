package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.business.service.sku.SkuCustomizationOptionService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.SelectionType;
import com.harmoni.pos.menu.model.SkuCustomizationOption;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceResponseDto;
import com.harmoni.pos.menu.model.dto.pricing.PricedCustomizationDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomizationPricingServiceImplTest {

    private static final String USERNAME = "cashier";
    private static final Integer STORE_ID = 7;
    private static final Integer TIER_ID = 3;
    private static final Integer PRODUCT_ID = 10;
    private static final Integer SKU_ID = 20;

    @Mock
    private UserService userService;
    @Mock
    private StoreTierService storeTierService;
    @Mock
    private ProductCustomizationService productCustomizationService;
    @Mock
    private SkuCustomizationOptionService skuCustomizationOptionService;
    @Mock
    private CustomizationOptionMapper customizationOptionMapper;
    @Mock
    private CustomizationOptionTierPriceService tierPriceService;

    private CustomizationPricingServiceImpl pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new CustomizationPricingServiceImpl(userService, storeTierService,
                productCustomizationService, skuCustomizationOptionService,
                customizationOptionMapper, tierPriceService, new CustomizationSelectionValidator());
    }

    private static CustomizationPriceRequestDto request(Integer... optionIds) {
        CustomizationPriceRequestDto request = new CustomizationPriceRequestDto();
        request.setProductId(PRODUCT_ID);
        request.setSkuId(SKU_ID);
        request.setQuantity(1);
        request.setCustomizationOptionIds(List.of(optionIds));
        return request;
    }

    private static SkuCustomizationOption link(Integer optionId) {
        SkuCustomizationOption link = new SkuCustomizationOption();
        link.setSkuId(SKU_ID);
        link.setCustomizationOptionId(optionId);
        return link;
    }

    private static CustomizationOption option(Integer id, Integer customizationId, String name) {
        CustomizationOption option = new CustomizationOption();
        option.setId(id);
        option.setCustomizationId(customizationId);
        option.setName(name);
        return option;
    }

    private static CustomizationOptionTierPrice tierPrice(Integer optionId, String price) {
        CustomizationOptionTierPrice tierPrice = new CustomizationOptionTierPrice();
        tierPrice.setCustomizationOptionId(optionId);
        tierPrice.setTierId(TIER_ID);
        tierPrice.setPrice(new BigDecimal(price));
        return tierPrice;
    }

    private static ProductCustomizationResponseDto group(Integer customizationId, String name,
                                                        SelectionType selectionType, Boolean required,
                                                        Integer min, Integer max) {
        ProductCustomizationResponseDto dto = new ProductCustomizationResponseDto();
        dto.setCustomizationId(customizationId);
        dto.setName(name);
        dto.setSelectionType(selectionType);
        dto.setRequired(required);
        // Existing tests predate the quantity flag and repeat options; keep them
        // expressing quantities by defaulting the catalogue fixture to allow it.
        dto.setAllowQuantity(true);
        dto.setMinSelection(min);
        dto.setMaxSelection(max);
        return dto;
    }

    private void givenStoreTier() {
        User user = new User();
        user.setStoreId(STORE_ID);
        StoreTier storeTier = new StoreTier();
        storeTier.setTierPriceId(TIER_ID);
        when(userService.selectByUsername(USERNAME)).thenReturn(user);
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(storeTier);
    }

    private void givenProductHasNoCustomizations() {
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID)).thenReturn(List.of());
    }

    @Test
    void price_shouldPriceChosenOptionsAtTheOperatorsTier() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID))
                .thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large"), option(102, 1, "Extra Cheese")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50"), tierPrice(102, "2.00")));

        CustomizationPriceResponseDto response = pricingService.price(request(101, 102), USERNAME);

        assertEquals(2, response.getCustomizations().size());
        assertEquals("Large", response.getCustomizations().get(0).getOptionName());
        assertEquals(new BigDecimal("1.50"), response.getCustomizations().get(0).getPrice());
        assertEquals(new BigDecimal("3.50"), response.getTotalAmount());
        assertEquals(PRODUCT_ID, response.getProductId());
        assertEquals(SKU_ID, response.getSkuId());
    }

    @Test
    void price_shouldChargeTheOptionPriceForEveryUnitOrdered() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceRequestDto request = request(101);
        request.setQuantity(4);

        CustomizationPriceResponseDto response = pricingService.price(request, USERNAME);

        assertEquals(new BigDecimal("1.50"), response.getCustomizations().get(0).getPrice());
        assertEquals(new BigDecimal("6.00"), response.getCustomizations().get(0).getAmount());
        assertEquals(new BigDecimal("6.00"), response.getTotalAmount());
    }

    @Test
    void price_shouldRejectAProductIdTheSkuCannotBeCustomisedWith() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));

        CustomizationPriceResponseDto response = pricingService.price(request(999), USERNAME);

        assertTrue(response.getCustomizations().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
        verify(customizationOptionMapper, never()).selectByIds(anyList());
    }

    @Test
    void price_shouldRejectOptionsTheSkuDoesNotOffer() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID))
                .thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large")));

        CustomizationPriceResponseDto response = pricingService.price(request(101, 999), USERNAME);

        assertEquals(1, response.getCustomizations().size());
        assertEquals(101, response.getCustomizations().get(0).getCustomizationOptionId());
    }

    @Test
    void price_shouldFallBackToEveryTierWhenTheStoreHasNoTier() {
        User user = new User();
        user.setStoreId(STORE_ID);
        when(userService.selectByUsername(USERNAME)).thenReturn(user);
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(null);
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large")));
        when(tierPriceService.selectByOptionIds(anyList()))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceResponseDto response = pricingService.price(request(101), USERNAME);

        assertEquals(new BigDecimal("1.50"), response.getTotalAmount());
        verify(tierPriceService).selectByOptionIds(anyList());
    }

    @Test
    void price_shouldTreatAMissingQuantityAsOneUnit() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceRequestDto request = request(101);
        request.setQuantity(null);

        CustomizationPriceResponseDto response = pricingService.price(request, USERNAME);

        assertEquals(new BigDecimal("1.50"), response.getTotalAmount());
    }

    @Test
    void price_shouldPriceARepeatedOptionIdAsAQuantity() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Extra Shot")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceResponseDto response = pricingService.price(request(101, 101, 101), USERNAME);

        assertEquals(1, response.getCustomizations().size());
        assertEquals(3, response.getCustomizations().get(0).getQuantity());
        assertEquals(new BigDecimal("4.50"), response.getCustomizations().get(0).getAmount());
        assertEquals(new BigDecimal("4.50"), response.getTotalAmount());
    }

    @Test
    void price_shouldChargeTheOptionPriceForEveryUnitOfARepeatedChoice() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Extra Shot")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceRequestDto request = request(101, 101);
        request.setQuantity(4);

        CustomizationPriceResponseDto response = pricingService.price(request, USERNAME);

        assertEquals(2, response.getCustomizations().get(0).getQuantity());
        assertEquals(new BigDecimal("12.00"), response.getTotalAmount());
    }

    @Test
    void price_shouldRejectTwoDistinctOptionsOnASingleSelectionCustomization() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Size", SelectionType.SINGLE, true, 1, 1)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID))
                .thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large"), option(102, 1, "Small")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50"), tierPrice(102, "1.00")));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101, 102), USERNAME));

        assertEquals("exception.customizationPrice.singleSelection", thrown.getMessage());
        assertEquals(2, thrown.getArgs()[1]);
    }

    @Test
    void price_shouldAcceptARepeatOnASingleSelectionGroupThatAllowsQuantity() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Size", SelectionType.SINGLE, true, 1, 1)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Large")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.50")));

        CustomizationPriceResponseDto response = pricingService.price(request(101, 101), USERNAME);

        assertEquals(2, response.getCustomizations().get(0).getQuantity());
        assertEquals(new BigDecimal("3.00"), response.getTotalAmount());
    }

    @Test
    void price_shouldCountDistinctOptionsTowardsTheMaximum() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Toppings", SelectionType.MULTIPLE, false, 0, 2)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID))
                .thenReturn(List.of(link(101), link(102), link(103)));
        when(customizationOptionMapper.selectByIds(anyList())).thenAnswer(invocation -> {
            List<Integer> wanted = invocation.getArgument(0);
            return List.of(option(101, 1, "Cheese"), option(102, 1, "Bacon"),
                    option(103, 1, "Egg")).stream()
                    .filter(option -> wanted.contains(option.getId()))
                    .toList();
        });
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00"), tierPrice(102, "1.00"),
                        tierPrice(103, "1.00")));

        CustomizationPriceResponseDto allowed = pricingService.price(request(101, 101), USERNAME);
        assertEquals(new BigDecimal("2.00"), allowed.getTotalAmount());

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101, 102, 103), USERNAME));
        assertEquals("exception.customizationPrice.aboveMaximum", thrown.getMessage());
        assertEquals(3, thrown.getArgs()[1]);
        assertEquals(2, thrown.getArgs()[2]);
    }

    @Test
    void price_shouldRejectARepeatWhenTheGroupDisallowsQuantity() {
        givenStoreTier();
        ProductCustomizationResponseDto toppings = group(1, "Toppings", SelectionType.MULTIPLE, false, 0, 5);
        toppings.setAllowQuantity(false);
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(toppings));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Cheese")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00")));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101, 101), USERNAME));

        assertEquals("exception.customizationPrice.quantityNotAllowed", thrown.getMessage());
        assertEquals(101, thrown.getArgs()[0]);
        assertEquals(2, thrown.getArgs()[1]);
    }

    @Test
    void price_shouldPriceARepeatWhenTheGroupAllowsQuantity() {
        givenStoreTier();
        ProductCustomizationResponseDto toppings = group(1, "Toppings", SelectionType.MULTIPLE, false, 0, 5);
        toppings.setAllowQuantity(true);
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(toppings));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Cheese")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00")));

        CustomizationPriceResponseDto response = pricingService.price(request(101, 101), USERNAME);

        assertEquals(2, response.getCustomizations().get(0).getQuantity());
        assertEquals(new BigDecimal("2.00"), response.getTotalAmount());
    }

    @Test
    void price_shouldCapRepeatsOfAnOptionWhenTheGroupHasNoMaximum() {
        Integer[] tooMany = new Integer[51];
        java.util.Arrays.fill(tooMany, 101);

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(tooMany), USERNAME));

        assertEquals("exception.customizationPrice.tooManyUnits", thrown.getMessage());
        assertEquals(51, thrown.getArgs()[1]);
    }

    @Test
    void price_shouldRejectARequestWithFarTooManyEntries() {
        Integer[] tooMany = new Integer[201];
        java.util.Arrays.fill(tooMany, 101);

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(tooMany), USERNAME));

        assertEquals("exception.customizationPrice.tooManyChoices", thrown.getMessage());
    }

    @Test
    void price_shouldPriceAnOptionWithNoTierPriceAsFree() {
        givenStoreTier();
        givenProductHasNoCustomizations();
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "No Extra")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of());

        CustomizationPriceResponseDto response = pricingService.price(request(101), USERNAME);

        assertEquals(1, response.getCustomizations().size());
        assertEquals("No Extra", response.getCustomizations().get(0).getOptionName());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
    }

    @Test
    void price_shouldRejectAMissingProductOrSku() {
        CustomizationPriceRequestDto request = new CustomizationPriceRequestDto();
        request.setSkuId(SKU_ID);

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request, USERNAME));

        assertEquals("exception.customizationPrice.productSkuRequired", thrown.getMessage());
        verify(userService, never()).selectByUsername(any());
    }

    @Test
    void price_shouldRejectAMissingRequiredChoice() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Size", SelectionType.SINGLE, true, 1, 1)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of());

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(), USERNAME));

        assertEquals("exception.customizationPrice.required", thrown.getMessage());
        assertEquals("Size", thrown.getArgs()[0]);
    }

    @Test
    void price_shouldRejectTwoChoicesOnASingleSelectionCustomization() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Size", SelectionType.SINGLE, true, 1, 1)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Small"), option(102, 1, "Large")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "0.00"), tierPrice(102, "1.50")));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101, 102), USERNAME));

        assertEquals("exception.customizationPrice.singleSelection", thrown.getMessage());
        assertEquals("Size", thrown.getArgs()[0]);
    }

    @Test
    void price_shouldRejectFewerChoicesThanTheMinimum() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Toppings", SelectionType.MULTIPLE, false, 2, 4)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Cheese")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00")));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101), USERNAME));

        assertEquals("exception.customizationPrice.belowMinimum", thrown.getMessage());
        assertEquals("Toppings", thrown.getArgs()[0]);
        assertEquals(1, thrown.getArgs()[1]);
        assertEquals(2, thrown.getArgs()[2]);
    }

    @Test
    void price_shouldRejectMoreChoicesThanTheMaximum() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Toppings", SelectionType.MULTIPLE, false, 0, 1)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Cheese"), option(102, 1, "Ham")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00"), tierPrice(102, "1.00")));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101, 102), USERNAME));

        assertEquals("exception.customizationPrice.aboveMaximum", thrown.getMessage());
        assertEquals("Toppings", thrown.getArgs()[0]);
        assertEquals(2, thrown.getArgs()[1]);
        assertEquals(1, thrown.getArgs()[2]);
    }

    @Test
    void price_shouldEnforceTheProductOverrideRatherThanTheCustomizationDefault() {
        givenStoreTier();
        ProductCustomizationResponseDto group = group(1, "Toppings", SelectionType.MULTIPLE, false, 2, 4);
        group.setMinSelectionOverride(2);
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID)).thenReturn(List.of(group));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101), link(102)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Cheese")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "1.00")));

        assertThrows(BusinessBadRequestException.class,
                () -> pricingService.price(request(101), USERNAME));
    }

    @Test
    void price_shouldAllowAChoiceOnACustomizationWithNoRules() {
        givenStoreTier();
        when(productCustomizationService.getDetailedByProductId(PRODUCT_ID))
                .thenReturn(List.of(group(1, "Notes", SelectionType.MULTIPLE, false, null, null)));
        when(skuCustomizationOptionService.getBySkuId(SKU_ID)).thenReturn(List.of(link(101)));
        when(customizationOptionMapper.selectByIds(anyList()))
                .thenReturn(List.of(option(101, 1, "Extra Sauce")));
        when(tierPriceService.selectByOptionIdsAndTierId(anyList(), eq(TIER_ID)))
                .thenReturn(List.of(tierPrice(101, "0.50")));

        CustomizationPriceResponseDto response = pricingService.price(request(101), USERNAME);

        assertEquals(new BigDecimal("0.50"), response.getTotalAmount());
    }
}