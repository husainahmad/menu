package com.harmoni.pos.business.service.validation;

import com.harmoni.pos.business.service.customization.CustomizationSelectionValidator;
import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.business.service.sku.SkuCustomizationOptionService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.SelectionType;
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
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsResponseDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedCustomizationDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedOrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderItemValidationServiceImplTest {

    private static final Integer STORE_ID = 1;
    private static final Integer PRICE_TIER_ID = 5;
    private static final Integer MENU_TIER_ID = 4;
    private static final Integer PRODUCT_ID = 10;
    private static final Integer SKU_ID = 101;
    private static final Integer CATEGORY_ID = 3;
    private static final Integer ICE_GROUP_ID = 100;
    private static final Integer MILK_GROUP_ID = 200;
    private static final Integer ICE_OPTION_ID = 1001;
    private static final Integer OAT_OPTION_ID = 1005;
    private static final Integer SOY_OPTION_ID = 1006;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private SkuMapper skuMapper;
    @Mock
    private StoreTierService storeTierService;
    @Mock
    private TierMenuService tierMenuService;
    @Mock
    private SkuTierPriceService skuTierPriceService;
    @Mock
    private ProductCustomizationService productCustomizationService;
    @Mock
    private SkuCustomizationOptionService skuCustomizationOptionService;
    @Mock
    private CustomizationOptionMapper customizationOptionMapper;
    @Mock
    private CustomizationOptionTierPriceService customizationOptionTierPriceService;

    private OrderItemValidationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderItemValidationServiceImpl(productMapper, skuMapper, storeTierService, tierMenuService,
                skuTierPriceService, productCustomizationService, skuCustomizationOptionService,
                customizationOptionMapper, customizationOptionTierPriceService,
                new CustomizationSelectionValidator());
    }

    // ------------------------------------------------------------------ fixtures

    private static ValidateOrderItemsRequest request(ValidateOrderItemRequest... lines) {
        ValidateOrderItemsRequest request = new ValidateOrderItemsRequest();
        request.setStoreId(STORE_ID);
        request.setItems(new ArrayList<>(Arrays.asList(lines)));
        return request;
    }

    private static ValidateOrderItemRequest line(Integer productId, Integer skuId, Integer quantity,
                                                 CustomizationGroupSelectionDto... groups) {
        ValidateOrderItemRequest line = new ValidateOrderItemRequest();
        line.setProductId(productId);
        line.setSkuId(skuId);
        line.setQuantity(quantity);
        line.setCustomizations(new ArrayList<>(Arrays.asList(groups)));
        return line;
    }

    private static CustomizationOptionSelectionDto option(Integer optionId, Integer quantity) {
        CustomizationOptionSelectionDto selection = new CustomizationOptionSelectionDto();
        selection.setCustomizationOptionId(optionId);
        selection.setQuantity(quantity);
        return selection;
    }

    private static CustomizationGroupSelectionDto group(Integer customizationId,
                                                        CustomizationOptionSelectionDto... options) {
        CustomizationGroupSelectionDto group = new CustomizationGroupSelectionDto();
        group.setCustomizationId(customizationId);
        group.setOptions(new ArrayList<>(Arrays.asList(options)));
        return group;
    }

    private static Integer groupIdFor(Integer optionId) {
        if (ICE_OPTION_ID.equals(optionId)) {
            return ICE_GROUP_ID;
        }
        if (OAT_OPTION_ID.equals(optionId) || SOY_OPTION_ID.equals(optionId)) {
            return MILK_GROUP_ID;
        }
        return ICE_GROUP_ID;
    }

    private static CustomizationGroupSelectionDto choice(Integer optionId, Integer quantity) {
        return group(groupIdFor(optionId), option(optionId, quantity));
    }

    private static Product product() {
        return new Product().setId(PRODUCT_ID).setName("Kopi Gula Aren").setCategoryId(CATEGORY_ID)
                .setCategory(new Category().setId(CATEGORY_ID).setName("Kopi"));
    }

    private static Sku sku() {
        return new Sku().setId(SKU_ID).setProductId(PRODUCT_ID).setName("Large")
                .setActive(true).setDeleted(false);
    }

    private static CustomizationOption option(Integer id, Integer groupId, String name) {
        return new CustomizationOption().setId(id).setCustomizationId(groupId).setName(name).setIsDeleted(false);
    }

    private static ProductCustomizationResponseDto group(Integer customizationId, String name,
                                                         SelectionType selectionType, Boolean required,
                                                         Integer min, Integer max,
                                                         CustomizationOption... options) {
        ProductCustomizationResponseDto group = new ProductCustomizationResponseDto();
        group.setCustomizationId(customizationId);
        group.setName(name);
        group.setSelectionType(selectionType);
        group.setRequired(required);
        // Existing tests predate the quantity flag and send quantities; keep them
        // expressing quantities by defaulting the catalogue fixture to allow it.
        group.setAllowQuantity(true);
        group.setMinSelection(min);
        group.setMaxSelection(max);
        group.setOptions(new ArrayList<>(Arrays.asList(options)));
        return group;
    }

    private static SkuCustomizationOption link(Integer optionId) {
        return new SkuCustomizationOption().setSkuId(SKU_ID).setCustomizationOptionId(optionId);
    }

    /**
     * A store with both tiers configured, selling one category, and a product and SKU that
     * are on sale and priced. Everything else each test needs is stubbed on top of this.
     */
    private void givenASellableBasket() {
        StoreTier storeTier = new StoreTier().setStoreId(STORE_ID).setTierPriceId(PRICE_TIER_ID)
                .setTierMenuId(9).setTierMenu(new TierMenu().setId(9).setTierId(MENU_TIER_ID));
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(storeTier);
        when(tierMenuService.getMenusByTierId(MENU_TIER_ID))
                .thenReturn(List.of(new TierMenu().setCategoryId(CATEGORY_ID).setActive(true).setDeleted(false)));
        when(productMapper.selectByIds(anyList(), anyInt()))
                .thenReturn(List.of(product()));
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku()));
        when(skuTierPriceService.selectBySkusTierId(anyList(), any()))
                .thenReturn(List.of(new SkuTierPrice().setSkuId(SKU_ID).setTierId(PRICE_TIER_ID)
                        .setPrice(new BigDecimal("20000")).setDeleted(false)));
    }

    private void givenACustomizableBasket() {
        givenASellableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(ICE_GROUP_ID, "Ice", SelectionType.SINGLE, Boolean.TRUE, 1, 1,
                        option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice")),
                group(MILK_GROUP_ID, "Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));
        when(skuCustomizationOptionService.getBySkuIds(anyList()))
                .thenReturn(List.of(link(ICE_OPTION_ID), link(OAT_OPTION_ID)));
        when(customizationOptionMapper.selectByIds(anyList())).thenReturn(List.of(
                option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice"),
                option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")));
        when(customizationOptionTierPriceService.selectByOptionIdsAndTierId(anyList(), any()))
                .thenReturn(List.of(
                        new CustomizationOptionTierPrice().setCustomizationOptionId(ICE_OPTION_ID)
                                .setTierId(PRICE_TIER_ID).setPrice(BigDecimal.ZERO),
                        new CustomizationOptionTierPrice().setCustomizationOptionId(OAT_OPTION_ID)
                                .setTierId(PRICE_TIER_ID).setPrice(new BigDecimal("4000"))));
    }

    private BusinessBadRequestException rejectionOf(ValidateOrderItemsRequest request) {
        return assertThrows(BusinessBadRequestException.class, () -> service.validate(request));
    }

    // ------------------------------------------------------------------- product

    @Test
    void validate_shouldReturnTheOfficialProductAndSkuDataAndTheRequestedQuantity() {
        givenASellableBasket();

        ValidateOrderItemsResponseDto response =
                service.validate(request(line(PRODUCT_ID, SKU_ID, 2)));

        assertTrue(response.getValid());
        assertEquals(1, response.getItems().size());
        ValidatedOrderItemDto item = response.getItems().get(0);
        assertEquals(PRODUCT_ID, item.getProductId());
        assertEquals("Kopi Gula Aren", item.getProductName());
        assertEquals(CATEGORY_ID, item.getCategoryId());
        assertEquals("Kopi", item.getCategoryName());
        assertEquals(SKU_ID, item.getSkuId());
        assertEquals("Large", item.getSkuName());
        assertEquals(0, new BigDecimal("20000").compareTo(item.getSkuPrice()));
        assertEquals(2, item.getQuantity());
        assertTrue(item.getCustomizations().isEmpty());
    }

    @Test
    void validate_shouldRejectAProductThatDoesNotExist() {
        givenASellableBasket();
        when(productMapper.selectByIds(anyList(), anyInt())).thenReturn(List.of());

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.productNotFound", e.getMessage());
    }

    @Test
    void validate_shouldRejectAProductWhoseCategoryIsNotOnTheStoreMenu() {
        givenASellableBasket();
        when(tierMenuService.getMenusByTierId(MENU_TIER_ID))
                .thenReturn(List.of(new TierMenu().setCategoryId(999).setActive(true).setDeleted(false)));

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.productNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldRejectEverythingWhenTheStoreHasSwitchedOffItsWholeMenu() {
        givenASellableBasket();
        // The menu tier is configured but has no active category: a deliberate decision to
        // sell nothing, which is not the same as a store that has no menu tier at all.
        when(tierMenuService.getMenusByTierId(MENU_TIER_ID)).thenReturn(List.of());

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.productNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldNotApplyTheCategoryGateWhenTheStoreHasNoMenuTier() {
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(new StoreTier().setTierPriceId(PRICE_TIER_ID));
        when(productMapper.selectByIds(anyList(), anyInt())).thenReturn(List.of(product()));
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku()));
        when(skuTierPriceService.selectBySkusTierId(anyList(), any()))
                .thenReturn(List.of(new SkuTierPrice().setSkuId(SKU_ID).setTierId(PRICE_TIER_ID)
                        .setPrice(new BigDecimal("20000"))));

        ValidateOrderItemsResponseDto response = service.validate(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("Kopi Gula Aren", response.getItems().get(0).getProductName());
        verify(tierMenuService, never()).getMenusByTierId(any());
    }

    // ----------------------------------------------------------------------- sku

    @Test
    void validate_shouldRejectASkuThatDoesNotExist() {
        givenASellableBasket();
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of());

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuNotFound", e.getMessage());
    }

    @Test
    void validate_shouldRejectADeletedSku() {
        givenASellableBasket();
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku().setDeleted(true)));

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldRejectAnInactiveSku() {
        givenASellableBasket();
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku().setActive(false)));

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldRejectASkuBelongingToAnotherProduct() {
        givenASellableBasket();
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku().setProductId(777)));

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuProductMismatch", e.getMessage());
    }

    @Test
    void validate_shouldRejectASkuWithNoPriceAtTheStore() {
        givenASellableBasket();
        when(skuTierPriceService.selectBySkusTierId(anyList(), any())).thenReturn(List.of());

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuPriceNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldIgnoreASoftDeletedTierPrice() {
        givenASellableBasket();
        when(skuTierPriceService.selectBySkusTierId(anyList(), any())).thenReturn(List.of(
                new SkuTierPrice().setSkuId(SKU_ID).setTierId(PRICE_TIER_ID)
                        .setPrice(new BigDecimal("20000")).setDeleted(true)));

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.orderValidation.skuPriceNotAvailable", e.getMessage());
    }

    @Test
    void validate_shouldReadTheSkuPriceAtTheStoresPriceTier() {
        givenASellableBasket();

        service.validate(request(line(PRODUCT_ID, SKU_ID, 1)));

        verify(skuTierPriceService).selectBySkusTierId(List.of(SKU_ID), PRICE_TIER_ID);
    }

    @Test
    void validate_shouldFallBackToTheLowestTierWhenTheStoreHasNoPriceTier() {
        when(storeTierService.selectByStoreId(STORE_ID)).thenReturn(new StoreTier().setStoreId(STORE_ID));
        when(productMapper.selectByIds(anyList(), anyInt())).thenReturn(List.of(product()));
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku()));
        when(skuTierPriceService.selectBySkusTierId(anyList(), any())).thenReturn(List.of(
                new SkuTierPrice().setSkuId(SKU_ID).setTierId(9).setPrice(new BigDecimal("99999")),
                new SkuTierPrice().setSkuId(SKU_ID).setTierId(2).setPrice(new BigDecimal("20000"))));

        ValidateOrderItemsResponseDto response = service.validate(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals(0, new BigDecimal("20000").compareTo(response.getItems().get(0).getSkuPrice()));
    }

    // ------------------------------------------------------------------ quantity

    @Test
    void validate_shouldRejectAQuantityOfZero() {
        givenASellableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 0)));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldRejectANegativeQuantity() {
        givenASellableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, -1)));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldRejectAMissingQuantity() {
        givenASellableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, null)));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldRejectAnAbsurdQuantity() {
        givenASellableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 5000)));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    // ----------------------------------------------------------- customizations

    @Test
    void validate_shouldReturnTheOfficialCustomizationDataAndTheRequestedQuantities() {
        givenACustomizableBasket();

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 2, choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        List<ValidatedCustomizationDto> customizations = response.getItems().get(0).getCustomizations();
        assertEquals(2, customizations.size());

        ValidatedCustomizationDto ice = customizations.get(0);
        assertEquals(ICE_OPTION_ID, ice.getCustomizationOptionId());
        assertEquals(ICE_GROUP_ID, ice.getCustomizationId());
        assertEquals("Ice", ice.getCustomizationName());
        assertEquals("Less Ice", ice.getOptionName());
        assertEquals(0, BigDecimal.ZERO.compareTo(ice.getPrice()));
        assertEquals(1, ice.getQuantity());

        ValidatedCustomizationDto oat = customizations.get(1);
        assertEquals(OAT_OPTION_ID, oat.getCustomizationOptionId());
        assertEquals("Milk", oat.getCustomizationName());
        assertEquals("Oat Milk", oat.getOptionName());
        assertEquals(0, new BigDecimal("4000").compareTo(oat.getPrice()));
        assertEquals(1, oat.getQuantity());
    }

    @Test
    void validate_shouldNotReturnAnyTotalForTheLine() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 3,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 2, choice(OAT_OPTION_ID, 3))));

        // The caller owns the arithmetic: nothing here is pre-multiplied.
        ValidatedCustomizationDto oat = response.getItems().get(0).getCustomizations().get(0);
        assertEquals(0, new BigDecimal("4000").compareTo(oat.getPrice()));
        assertEquals(3, oat.getQuantity());
    }

    @Test
    void validate_shouldSumARerepeatedOptionIntoOneQuantity() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 3,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 2), choice(OAT_OPTION_ID, 1))));

        List<ValidatedCustomizationDto> customizations = response.getItems().get(0).getCustomizations();
        assertEquals(1, customizations.size());
        assertEquals(3, customizations.get(0).getQuantity());
    }

    @Test
    void validate_shouldRejectAnOptionThatDoesNotExist() {
        givenACustomizableBasket();
        when(customizationOptionMapper.selectByIds(anyList())).thenReturn(List.of(
                option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice")));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1), choice(9999, 1))));

        assertEquals("exception.orderValidation.customizationOptionNotFound", e.getMessage());
    }

    @Test
    void validate_shouldRejectAnOptionTheSkuDoesNotPermit() {
        givenACustomizableBasket();
        when(skuCustomizationOptionService.getBySkuIds(anyList())).thenReturn(List.of(link(ICE_OPTION_ID)));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getCustomizations().size());
    }

    @Test
    void validate_shouldRejectAnOptionThatBelongsToAnotherProduct() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList()))
                .thenReturn(Map.of(PRODUCT_ID, List.of(
                        group(ICE_GROUP_ID, "Ice", SelectionType.SINGLE, Boolean.TRUE, 1, 1,
                                option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice")))));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        assertEquals("exception.orderValidation.customizationNotApplicable", e.getMessage());
    }

    @Test
    void validate_shouldRejectAMissingRequiredCustomization() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.customizationPrice.required", e.getMessage());
    }

    @Test
    void validate_shouldRejectFewerSelectionsThanTheCustomizationMinimum() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, 2, 3,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 1))));

        assertEquals("exception.customizationPrice.belowMinimum", e.getMessage());
    }

    @Test
    void validate_shouldRejectMoreSelectionsThanTheCustomizationMaximum() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"),
                        option(SOY_OPTION_ID, MILK_GROUP_ID, "Soy Milk")))));
        when(customizationOptionMapper.selectByIds(anyList())).thenReturn(List.of(
                option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"),
                option(SOY_OPTION_ID, MILK_GROUP_ID, "Soy Milk")));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 1), choice(SOY_OPTION_ID, 1))));

        assertEquals("exception.customizationPrice.aboveMaximum", e.getMessage());
    }

    @Test
    void validate_shouldNotCountUnitsTowardsTheMaximum() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 3))));

        assertEquals(3, response.getItems().get(0).getCustomizations().get(0).getQuantity());
    }

    @Test
    void validate_shouldRejectTwoDistinctOptionsOnASingleSelectCustomization() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"),
                        option(SOY_OPTION_ID, MILK_GROUP_ID, "Soy Milk")))));
        when(customizationOptionMapper.selectByIds(anyList())).thenReturn(List.of(
                option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"),
                option(SOY_OPTION_ID, MILK_GROUP_ID, "Soy Milk")));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 1), choice(SOY_OPTION_ID, 1))));

        assertEquals("exception.customizationPrice.singleSelection", e.getMessage());
    }

    @Test
    void validate_shouldAcceptAQuantityGreaterThanOneOnASingleSelectGroupThatAllowsIt() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 2))));

        assertEquals(2, response.getItems().get(0).getCustomizations().get(0).getQuantity());
    }

    @Test
    void validate_shouldNotLetUnitsSatisfyTheMinimum() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, 2, 3,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 3))));

        assertEquals("exception.customizationPrice.belowMinimum", e.getMessage());
    }

    @Test
    void validate_shouldRejectACustomizationQuantityOfZero() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 0))));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldRejectAnAbsurdCustomizationQuantity() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 500))));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldAcceptALineWithNoChoicesWhenNoCustomizationIsRequired() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        ValidateOrderItemsResponseDto response = service.validate(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertTrue(response.getItems().get(0).getCustomizations().isEmpty());
    }

    @Test
void validate_shouldNotPriceAnyOptionForALineThatNamedNone() {
        givenASellableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(MILK_GROUP_ID, "Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));

        service.validate(request(line(PRODUCT_ID, SKU_ID, 1)));

        // The product's customizations are still read, because that is how a required
        // choice is enforced. Nothing beyond that is: no option, no link, no price.
        verify(productCustomizationService, times(1)).getDetailedByProductIds(anyList());
        verify(customizationOptionMapper, never()).selectByIds(anyList());
        verify(customizationOptionTierPriceService, never()).selectByOptionIdsAndTierId(anyList(), any());
    }

    // ------------------------------------------------------------------ requests

    @Test
    void validate_shouldReadTheProductAndSkuLookupsOnceForTheWholeBasket() {
        givenASellableBasket();

        service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1),
                line(PRODUCT_ID, SKU_ID, 2),
                line(PRODUCT_ID, SKU_ID, 3)));

        ArgumentCaptor<List<Integer>> productIds = ArgumentCaptor.forClass(List.class);
        verify(productMapper).selectByIds(productIds.capture(), anyInt());
        assertEquals(List.of(PRODUCT_ID), productIds.getValue());
        verify(skuMapper, org.mockito.Mockito.times(1)).selectByIds(List.of(SKU_ID));
        verify(skuTierPriceService).selectBySkusTierId(List.of(SKU_ID), PRICE_TIER_ID);
    }

    @Test
    void validate_shouldKeepTheLinesInTheOrderTheyWereSent() {
        givenASellableBasket();
        Product second = new Product().setId(20).setName("Teh Tarik").setCategoryId(CATEGORY_ID);
        Sku secondSku = new Sku().setId(202).setProductId(20).setName("Small").setActive(true);
        when(productMapper.selectByIds(anyList(), anyInt())).thenReturn(List.of(product(), second));
        when(skuMapper.selectByIds(anyList())).thenReturn(List.of(sku(), secondSku));
        when(skuTierPriceService.selectBySkusTierId(anyList(), any())).thenReturn(List.of(
                new SkuTierPrice().setSkuId(SKU_ID).setTierId(PRICE_TIER_ID).setPrice(new BigDecimal("20000")),
                new SkuTierPrice().setSkuId(202).setTierId(PRICE_TIER_ID).setPrice(new BigDecimal("15000"))));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1), line(20, 202, 1)));

        assertEquals("Kopi Gula Aren", response.getItems().get(0).getProductName());
        assertEquals("Teh Tarik", response.getItems().get(1).getProductName());
        assertEquals(0, new BigDecimal("15000").compareTo(response.getItems().get(1).getSkuPrice()));
    }

    @Test
    void validate_shouldRejectABasketLargerThanTheRequestCeiling() {
        givenASellableBasket();
        List<ValidateOrderItemRequest> lines = new ArrayList<>();
        for (int i = 0; i < 201; i++) {
            lines.add(line(PRODUCT_ID, SKU_ID, 1));
        }

        BusinessBadRequestException e = rejectionOf(request(lines.toArray(new ValidateOrderItemRequest[0])));

        assertEquals("exception.orderValidation.tooManyLines", e.getMessage());
        verify(productMapper, never()).selectByIds(anyList(), anyInt());
    }

    @Test
    void validate_shouldRejectABasketThatCountsMoreChoicesThanLines() {
        givenACustomizableBasket();
        List<CustomizationGroupSelectionDto> choices = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            choices.add(choice(OAT_OPTION_ID, 1));
        }

        BusinessBadRequestException e = rejectionOf(
                request(line(PRODUCT_ID, SKU_ID, 1, choices.toArray(new CustomizationGroupSelectionDto[0]))));

        assertEquals("exception.orderValidation.tooManyLines", e.getMessage());
        verify(customizationOptionMapper, never()).selectByIds(anyList());
    }

    @Test
    void validate_shouldCarryNoSubtotalOrTotalAnywhereInTheAnswer() {
        givenACustomizableBasket();

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 2, choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        ValidatedOrderItemDto item = response.getItems().get(0);
        assertNull(readProperty(item, "amount"));
        assertNull(readProperty(item, "subtotal"));
        assertNull(readProperty(item, "totalAmount"));
        for (ValidatedCustomizationDto customization : item.getCustomizations()) {
            assertNull(readProperty(customization, "amount"));
        }
    }

    /**
     * Reflectively reads a property. A getter that does not exist reads as absent, which
     * is what this is checking for: a total that has crept onto a response DTO would show
     * up here instead of being discovered by a caller handed a subtotal it did not ask
     * for.
     */
    private static Object readProperty(Object target, String name) {
        try {
            return target.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0))
                    + name.substring(1)).invoke(target);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("could not read " + name, e);
        }
    }

    @Test
    void validate_shouldRejectAChoiceOnAProductThatOffersNoCustomizations() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of()));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 1))));

        assertEquals("exception.orderValidation.customizationNotApplicable", e.getMessage());
    }

    @Test
    void validate_shouldRejectAnOptionSentUnderTheWrongGroup() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, group(ICE_GROUP_ID, option(OAT_OPTION_ID, 1)))));

        assertEquals("exception.orderValidation.customizationNotApplicable", e.getMessage());
    }

    @Test
    void validate_shouldRejectAQuantityGreaterThanOneWhenTheGroupDisallowsIt() {
        givenACustomizableBasket();
        ProductCustomizationResponseDto milk = group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE,
                Boolean.FALSE, null, 3, option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"));
        milk.setAllowQuantity(false);
        when(productCustomizationService.getDetailedByProductIds(anyList()))
                .thenReturn(Map.of(PRODUCT_ID, List.of(milk)));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 2))));

        assertEquals("exception.orderValidation.quantityNotAllowed", e.getMessage());
    }

    @Test
    void validate_shouldRejectARepeatedOptionSummedAcrossGroupsWhenQuantityIsDisallowed() {
        givenACustomizableBasket();
        ProductCustomizationResponseDto milk = group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE,
                Boolean.FALSE, null, 3, option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"));
        milk.setAllowQuantity(false);
        when(productCustomizationService.getDetailedByProductIds(anyList()))
                .thenReturn(Map.of(PRODUCT_ID, List.of(milk)));

        BusinessBadRequestException e = rejectionOf(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        assertEquals("exception.orderValidation.quantityNotAllowed", e.getMessage());
    }

    @Test
    void validate_shouldAcceptAQuantityGreaterThanOneWhenTheGroupAllowsIt() {
        givenACustomizableBasket();
        ProductCustomizationResponseDto milk = group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE,
                Boolean.FALSE, null, 3, option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk"));
        milk.setAllowQuantity(true);
        when(productCustomizationService.getDetailedByProductIds(anyList()))
                .thenReturn(Map.of(PRODUCT_ID, List.of(milk)));

        ValidateOrderItemsResponseDto response = service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(OAT_OPTION_ID, 2))));

        assertEquals(2, response.getItems().get(0).getCustomizations().get(0).getQuantity());
    }

    @Test
    void validate_shouldEnforceARequiredCustomizationEvenWhenNoChoiceWasSent() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 1)));

        assertEquals("exception.customizationPrice.required", e.getMessage());
    }

    @Test
    void validate_shouldReadTheProductsCustomizationsOnceForRepeatedLines() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(ICE_GROUP_ID, "Ice", SelectionType.SINGLE, Boolean.TRUE, 1, 1,
                        option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice")))));

        service.validate(request(
                line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1)),
                line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1))));

        verify(productCustomizationService, org.mockito.Mockito.times(1)).getDetailedByProductIds(anyList());
    }

    /**
     * The whole point of the up front read: the number of queries follows the number of
     * tables, not the size of the basket. Forty lines cost exactly what one line does.
     * <p>
     * Written as a single test over a big basket on purpose. The per product and per SKU
     * lookups it asserts against are the ones this service used to make lazily inside the
     * line loop, and a lazy cache looks harmless until every line names a different product,
     * which is what a real basket mostly is. If someone reintroduces a lookup per line, this
     * fails rather than the behaviour quietly degrading at the till.
     */
    @Test
    void validate_shouldReadEveryTablesStateOnceNoMatterHowManyLinesTheBasketHas() {
        givenACustomizableBasket();
        int lines = 40;
        List<ValidateOrderItemRequest> basket = new ArrayList<>();
        for (int index = 0; index < lines; index++) {
            basket.add(line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1)));
        }

        service.validate(request(basket.toArray(new ValidateOrderItemRequest[0])));

        verify(storeTierService, times(1)).selectByStoreId(STORE_ID);
        verify(productMapper, times(1)).selectByIds(anyList(), anyInt());
        verify(skuMapper, times(1)).selectByIds(anyList());
        verify(tierMenuService, times(1)).getMenusByTierId(MENU_TIER_ID);
        verify(skuTierPriceService, times(1)).selectBySkusTierId(anyList(), any());
        verify(productCustomizationService, times(1)).getDetailedByProductIds(anyList());
        verify(skuCustomizationOptionService, times(1)).getBySkuIds(anyList());
        verify(customizationOptionMapper, times(1)).selectByIds(anyList());
        verify(customizationOptionTierPriceService, times(1)).selectByOptionIdsAndTierId(anyList(), any());

        // And the per id lookups this replaced must stay unused, or the counts above are
        // counting the wrong calls.
        verify(productCustomizationService, never()).getDetailedByProductId(any());
        verify(skuCustomizationOptionService, never()).getBySkuId(any());
    }

    /**
     * A line the basket refuses is still refused, which is why the reads being up front costs
     * nothing but a little work on a request that was going to fail anyway.
     */
    @Test
    void validate_shouldStillRefuseALineWithABadQuantityEvenThoughItsChoicesWereAlreadyRead() {
        givenACustomizableBasket();

        BusinessBadRequestException e = rejectionOf(request(line(PRODUCT_ID, SKU_ID, 0, choice(OAT_OPTION_ID, 1))));

        assertEquals("exception.orderValidation.invalidQuantity", e.getMessage());
    }

    @Test
    void validate_shouldNotPriceAnOptionTheCallerDidNotChoose() {
        givenACustomizableBasket();
        ArgumentCaptor<List<Integer>> priced = ArgumentCaptor.forClass(List.class);

        service.validate(request(line(PRODUCT_ID, SKU_ID, 1, choice(ICE_OPTION_ID, 1))));

        verify(customizationOptionTierPriceService).selectByOptionIdsAndTierId(priced.capture(),
                org.mockito.ArgumentMatchers.eq(PRICE_TIER_ID));
        assertEquals(List.of(ICE_OPTION_ID), priced.getValue());
    }

    @Test
    void validate_shouldReadOnePricePerLineRatherThanOnePerChoice() {
        givenACustomizableBasket();
        when(productCustomizationService.getDetailedByProductIds(anyList())).thenReturn(Map.of(PRODUCT_ID, List.of(
                group(ICE_GROUP_ID, "Ice", SelectionType.SINGLE, Boolean.TRUE, 1, 1,
                        option(ICE_OPTION_ID, ICE_GROUP_ID, "Less Ice")),
                group(MILK_GROUP_ID, "Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 3,
                        option(OAT_OPTION_ID, MILK_GROUP_ID, "Oat Milk")))));
        ArgumentCaptor<List<Integer>> priced = ArgumentCaptor.forClass(List.class);

        service.validate(request(line(PRODUCT_ID, SKU_ID, 1,
                choice(ICE_OPTION_ID, 1), choice(OAT_OPTION_ID, 1))));

        verify(customizationOptionTierPriceService, times(1))
                .selectByOptionIdsAndTierId(priced.capture(), eq(PRICE_TIER_ID));
        assertEquals(List.of(ICE_OPTION_ID, OAT_OPTION_ID), priced.getValue());
    }
}