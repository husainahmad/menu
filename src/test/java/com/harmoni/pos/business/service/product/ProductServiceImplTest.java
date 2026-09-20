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
