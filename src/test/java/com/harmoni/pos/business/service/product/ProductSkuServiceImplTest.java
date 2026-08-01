package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.product.image.ProductImageService;
import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
import com.harmoni.pos.menu.model.dto.edit.SkuEditDto;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import com.harmoni.pos.menu.model.dto.edit.SkuTierPriceEditDto;
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
class ProductSkuServiceImplTest {

    @Mock
    private ProductService productService;

    @Mock
    private SkuService skuService;

    @Mock
    private SkuTierPriceService skuTierPriceService;

    @Mock
    private ProductImageService productImageService;

    @InjectMocks
    private ProductSkuServiceImpl productSkuService;

    private ProductAddDto productAddDto;
    private ProductEditDto productEditDto;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product().setId(1).setName("Burger");

        SkuAddDto skuAddDto = new SkuAddDto();
        skuAddDto.setName("Small");
        SkuTierPriceDto priceDto = new SkuTierPriceDto();
        priceDto.setTierId(1);
        priceDto.setPrice(BigDecimal.valueOf(5.99));
        skuAddDto.setSkuTierPriceDtos(List.of(priceDto));

        productAddDto = new ProductAddDto();
        productAddDto.setName("Burger");
        productAddDto.setCategoryId(10);
        productAddDto.setSkuDtos(List.of(skuAddDto));
        productAddDto.setProductImageEditDto(null);

        SkuTierPriceEditDto priceEditDto = new SkuTierPriceEditDto();
        priceEditDto.setTierId(1);
        priceEditDto.setPrice(BigDecimal.valueOf(7.99));

        SkuEditDto skuEditDto = new SkuEditDto();
        skuEditDto.setName("Large");
        skuEditDto.setSkuTierPriceDtos(List.of(priceEditDto));

        productEditDto = new ProductEditDto();
        productEditDto.setId(1);
        productEditDto.setName("Burger");
        productEditDto.setCategoryId(10);
        productEditDto.setSkuDtos(List.of(skuEditDto));
        productEditDto.setProductImageEditDto(null);
    }

    @Test
    void create_shouldCreateProductAndSkus() {
        when(productService.create(productAddDto)).thenReturn(product);
        when(skuService.insertOrUpdate(any(Sku.class))).thenReturn(1);
        when(productImageService.updateByProductId(eq(1), isNull())).thenReturn(0);

        Product result = productSkuService.create(productAddDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(skuService).insertOrUpdate(any(Sku.class));
        verify(skuTierPriceService).insetOrUpdateBulk(anyList());
    }

    @Test
    void update_shouldUpdateProductAndSkus() {
        when(productService.update(productEditDto)).thenReturn(product);
        when(productImageService.updateByProductId(eq(1), isNull())).thenReturn(0);

        Product result = productSkuService.update(productEditDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(skuService).updateByIdBulk(anyList());
        verify(skuTierPriceService).insetOrUpdateBulk(anyList());
    }
}
