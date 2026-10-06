package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.business.service.product.image.ProductImageService;
import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;
import com.harmoni.pos.menu.model.dto.ProductSkuTierDto;
import com.harmoni.pos.menu.model.dto.ProductSkuTierPriceDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SkuService skuService;

    @Mock
    private TierService tierService;

    @Mock
    private SkuTierPriceService skuTierPriceService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductImageService productImageService;

    @Mock
    private com.harmoni.pos.menu.mapper.CustomizationMapper customizationMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.CustomizationOptionMapper customizationOptionMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.CustomizationOptionTierPriceMapper customizationOptionTierPriceMapper;

    @Mock
    private ProductCustomizationService productCustomizationService;

    @Mock
    private UserService userService;

    @Mock
    private StoreTierService storeTierService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductAddDto productAddDto;
    private ProductEditDto productEditDto;

    @BeforeEach
    void setUp() {
        product = new Product().setId(1).setName("Burger").setCategoryId(10);

        productAddDto = new ProductAddDto();
        productAddDto.setName("Burger");
        productAddDto.setCategoryId(10);

        productEditDto = new ProductEditDto();
        productEditDto.setId(1);
        productEditDto.setName("Burger");
        productEditDto.setCategoryId(10);
    }

    @Test
    void create_shouldSucceed() {
        when(productMapper.selectByNameCategoryId("Burger", 10)).thenReturn(null);
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        Product result = productService.create(productAddDto);
        assertNotNull(result);
        assertEquals("Burger", result.getName());
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(productMapper.selectByNameCategoryId("Burger", 10)).thenReturn(product);
        assertThrows(BusinessBadRequestException.class, () -> productService.create(productAddDto));
    }

    @Test
    void get_shouldReturnProduct() {
        when(productMapper.selectByPrimaryKey(1)).thenReturn(product);
        when(productImageService.selectByProductId(1)).thenReturn(null);

        Product result = productService.get(1);
        assertNotNull(result);
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(productMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessBadRequestException.class, () -> productService.get(1));
    }

    @Test
    void selectByCategory_shouldReturnList() {
        when(productMapper.selectByCategoryId(10)).thenReturn(List.of(product));
        assertFalse(productService.selectByCategory(10).isEmpty());
    }

    @Test
    void selectByCategoryPrice_shouldReturnList() {
        User user = new User().setId(1).setStoreId(5);
        StoreTier storeTier = new StoreTier().setTierPriceId(20);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(storeTierService.selectByStoreId(5)).thenReturn(storeTier);
        when(productMapper.selectByCategoryIdPrice(10, 20)).thenReturn(List.of(product));

        List<Product> result = productService.selectByCategoryPrice("Bearer token123", 10);
        assertEquals(1, result.size());
    }


    private static Customization customization(Integer id, String name, Boolean required,
                                               Integer min, Integer max) {
        return new Customization()
                .setId(id)
                .setName(name)
                .setSelectionType(SelectionType.MULTIPLE)
                .setRequired(required)
                .setMinimumSelection(min)
                .setMaximumSelection(max);
    }

    private static ProductCustomization link(Integer productId, Integer customizationId,
                                             Boolean requiredOverride, Integer minOverride,
                                             Integer maxOverride) {
        return new ProductCustomization()
                .setProductId(productId)
                .setCustomizationId(customizationId)
                .setRequiredOverride(requiredOverride)
                .setMinSelectionOverride(minOverride)
                .setMaxSelectionOverride(maxOverride);
    }

    private void givenCategoryPricing(List<Product> products) {
        User user = new User().setId(1).setStoreId(5);
        StoreTier storeTier = new StoreTier().setTierPriceId(20);
        when(userService.selectByUsername("cashier")).thenReturn(user);
        when(storeTierService.selectByStoreId(5)).thenReturn(storeTier);
        when(productMapper.selectByCategoryIdPrice(10, 20)).thenReturn(products);
    }

    @Test
    void selectByCategoryPrice_shouldAttachCustomizationsWithTheirOptions() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        givenCategoryPricing(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, null, null, null)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 4)));
        var option = new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese");
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(option));

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        var attached = result.get(0).getCustomizations();
        assertEquals(1, attached.size());
        assertEquals("Toppings", attached.get(0).getName());
        assertEquals(1, attached.get(0).getCustomizationOptions().size());
        assertEquals("Cheese", attached.get(0).getCustomizationOptions().get(0).getName());
    }

    @Test
    void selectByCategoryPrice_shouldApplyTheProductLevelOverrides() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        givenCategoryPricing(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, true, 3, 5)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 1)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        var attached = result.get(0).getCustomizations().get(0);
        assertTrue(attached.getRequired(), "the product override must win over the customization default");
        assertEquals(3, attached.getMinimumSelection());
        assertEquals(5, attached.getMaximumSelection());
    }

    @Test
    void selectByCategoryPrice_shouldFallBackToTheCustomizationDefaultsWhenNoOverride() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        givenCategoryPricing(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, null, null, null)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", true, 1, 2)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        var attached = result.get(0).getCustomizations().get(0);
        assertTrue(attached.getRequired());
        assertEquals(1, attached.getMinimumSelection());
        assertEquals(2, attached.getMaximumSelection());
    }

    @Test
    void selectByCategoryPrice_shouldNotLeakOneProductsOverrideOntoAnother() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        var wrap = new Product().setId(2).setName("Wrap").setCategoryId(10);
        givenCategoryPricing(List.of(burger, wrap));
        when(productCustomizationService.getByProductIds(List.of(1, 2)))
                .thenReturn(List.of(link(1, 100, true, 3, 5), link(2, 100, null, null, null)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 1)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        var forBurger = result.get(0).getCustomizations().get(0);
        var forWrap = result.get(1).getCustomizations().get(0);
        assertTrue(forBurger.getRequired(), "the burger demands three toppings");
        assertEquals(3, forBurger.getMinimumSelection());
        assertFalse(forWrap.getRequired(), "the wrap keeps the customization's own default");
        assertEquals(0, forWrap.getMinimumSelection());
        assertEquals(1, forWrap.getMaximumSelection());
    }

    @Test
    void selectByCategoryPrice_shouldAttachOnlyTheOperatorsTierPrices() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        givenCategoryPricing(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, null, null, null)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 1)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));
        var tierPrice = new CustomizationOptionTierPrice()
                .setCustomizationOptionId(1001).setTierId(20).setPrice(new BigDecimal("1.50"));
        when(customizationOptionTierPriceMapper.selectByOptionIdsAndTierId(List.of(1001), 20))
                .thenReturn(List.of(tierPrice));

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        var option = result.get(0).getCustomizations().get(0).getCustomizationOptions().get(0);
        assertEquals(1, option.getTierPrices().size());
        assertEquals(0, new BigDecimal("1.50").compareTo(option.getTierPrices().get(0).getPrice()));
    }

    @Test
    void selectByCategoryPrice_shouldAttachAnEmptyListForAProductWithNoCustomizations() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        givenCategoryPricing(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1))).thenReturn(List.of());

        List<Product> result = productService.selectByCategoryPrice("cashier", 10);

        assertNotNull(result.get(0).getCustomizations());
        assertTrue(result.get(0).getCustomizations().isEmpty());
    }


    @Test
    void selectByCategory_shouldAttachCustomizationsWithEveryTierPrice() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        when(productMapper.selectByCategoryId(10)).thenReturn(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, null, null, null)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 1)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));
        when(customizationOptionTierPriceMapper.selectByOptionIds(List.of(1001)))
                .thenReturn(List.of(
                        new CustomizationOptionTierPrice()
                                .setCustomizationOptionId(1001).setTierId(20).setPrice(new BigDecimal("1.50")),
                        new CustomizationOptionTierPrice()
                                .setCustomizationOptionId(1001).setTierId(21).setPrice(new BigDecimal("2.00"))));

        List<Product> result = productService.selectByCategory(10);

        var option = result.get(0).getCustomizations().get(0).getCustomizationOptions().get(0);
        assertEquals(2, option.getTierPrices().size(),
                "this endpoint has no operator, so every tier's price belongs in the answer");
    }

    @Test
    void selectByCategory_shouldApplyTheProductLevelOverrides() {
        var burger = new Product().setId(1).setName("Burger").setCategoryId(10);
        when(productMapper.selectByCategoryId(10)).thenReturn(List.of(burger));
        when(productCustomizationService.getByProductIds(List.of(1)))
                .thenReturn(List.of(link(1, 100, true, 2, 4)));
        when(customizationMapper.selectByIds(List.of(100)))
                .thenReturn(List.of(customization(100, "Toppings", false, 0, 1)));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(100)))
                .thenReturn(List.of(new CustomizationOption().setId(1001).setCustomizationId(100).setName("Cheese")));

        List<Product> result = productService.selectByCategory(10);

        var attached = result.get(0).getCustomizations().get(0);
        assertTrue(attached.getRequired());
        assertEquals(2, attached.getMinimumSelection());
        assertEquals(4, attached.getMaximumSelection());
    }

    @Test
    void delete_shouldSucceed() {
        doNothing().when(skuService).deleteSkuByProductId(1);
        when(productMapper.deleteByPrimaryKey(eq(1), eq(true), any())).thenReturn(1);

        assertEquals(1, productService.delete(1));
    }

    @Test
    void update_shouldSucceed() {
        when(productMapper.selectByNameCategoryId("Burger", 10)).thenReturn(null);
        when(productMapper.updateByPrimaryKey(any(Product.class))).thenReturn(1);

        Product result = productService.update(productEditDto);
        assertNotNull(result);
    }

    @Test
    void selectByNameCategoryId_shouldThrow_whenDuplicateExists() {
        when(productMapper.selectByNameCategoryId("Burger", 10)).thenReturn(product);
        assertThrows(BusinessBadRequestException.class,
                () -> productService.selectByNameCategoryId(null, "Burger", 10));
    }

    @Test
    void selectByNameCategoryId_shouldNotThrow_whenUpdatingSameProduct() {
        when(productMapper.selectByNameCategoryId("Burger", 10)).thenReturn(product);
        assertDoesNotThrow(() -> productService.selectByNameCategoryId(1, "Burger", 10));
    }

    @Test
    void getByList_shouldReturnList() {
        User user = new User();
        Store store = new Store();
        Chain chain = new Chain().setBrandId(1);
        store.setChain(chain);
        user.setStore(store);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(productMapper.selectByIds(List.of(1), 1)).thenReturn(List.of(product));

        List<Product> result = productService.getByList(List.of(1), "Bearer token123");
        assertEquals(1, result.size());
    }

    @Test
    void updateProductSku_shouldUpdateProductAndSkus() {
        Category category = new Category().setId(10);
        product.setCategoryId(10);

        ProductSkuTierPriceDto tierPrice = ProductSkuTierPriceDto.builder()
                .id(100)
                .price(BigDecimal.valueOf(5.99))
                .build();

        ProductSkuTierDto skuDto = ProductSkuTierDto.builder()
                .id(1)
                .name("Small")
                .tierPrice(tierPrice)
                .build();

        ProductSkuDto productSkuDto = ProductSkuDto.builder()
                .id(1)
                .name("Burger")
                .categoryId(10)
                .skus(List.of(skuDto))
                .build();

        when(productMapper.selectByPrimaryKey(1)).thenReturn(product);
        when(categoryService.get(10)).thenReturn(category);
        when(skuService.selectByProductId(1)).thenReturn(List.of());
        when(skuService.compareListSkus(anyList(), anyList())).thenReturn(List.of());
        when(tierService.validateTierByIds(anyList())).thenReturn(List.of());

        assertDoesNotThrow(() -> productService.updateProductSku(1, productSkuDto));
        verify(skuService).updateBulk(anyList());
        verify(skuTierPriceService).insetOrUpdateBulk(anyList());
    }
}
