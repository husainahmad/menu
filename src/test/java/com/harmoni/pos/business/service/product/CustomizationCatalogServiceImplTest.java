package com.harmoni.pos.business.service.product;

import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductDto;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductSkuDto;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomizationCatalogServiceImplTest {

    @Mock
    private ProductCustomizationMapper productCustomizationMapper;

    @InjectMocks
    private CustomizationCatalogServiceImpl service;

    @Test
    void selectByCustomization_shouldReturnProductsWithSkus() {
        when(productCustomizationMapper.selectProductsByCustomizationId(eq(12), isNull(), eq(30)))
                .thenReturn(List.of(new CustomizationProductDto()
                        .setId(101)
                        .setName("Burger")
                        .setCategoryId(7)
                        .setBrandId(3)
                        .setSkus(List.of(new CustomizationProductSkuDto().setId(9).setName("Large")))));

        CustomizationProductPage page = service.selectByCustomization(12, null, CallerScope.global());

        assertEquals(1, page.getProducts().size());
        assertEquals("Burger", page.getProducts().get(0).getName());
        assertEquals(7, page.getProducts().get(0).getCategoryId());
        assertEquals("Large", page.getProducts().get(0).getSkus().get(0).getName());
        assertNull(page.getNextCursor());
        assertFalse(page.isHasMore());
    }

    @Test
    void selectByCustomization_shouldCapLimitAtThirty() {
        when(productCustomizationMapper.selectProductsByCustomizationId(eq(12), isNull(), eq(30)))
                .thenReturn(List.of());

        service.selectByCustomization(12, 500, CallerScope.global());

        ArgumentCaptor<Integer> limit = ArgumentCaptor.forClass(Integer.class);
        verify(productCustomizationMapper).selectProductsByCustomizationId(eq(12), isNull(), limit.capture());
        assertEquals(30, limit.getValue());
    }

    @Test
    void selectByCustomization_shouldPassCallerBrandsAndDropOtherBrands() {
        CallerScope scope = CallerScope.of(Set.of(3), Set.of(), Set.of());
        when(productCustomizationMapper.selectProductsByCustomizationId(eq(12), eq(List.of(3)), eq(10)))
                .thenReturn(List.of(
                        new CustomizationProductDto().setId(101).setBrandId(3)
                                .setSkus(List.of(new CustomizationProductSkuDto().setId(9).setName("Large"))),
                        new CustomizationProductDto().setId(102).setBrandId(99)
                                .setSkus(List.of(new CustomizationProductSkuDto().setId(10).setName("Small")))));

        CustomizationProductPage page = service.selectByCustomization(12, 10, scope);

        assertEquals(List.of(101), page.getProducts().stream().map(CustomizationProductDto::getId).toList());
    }

    @Test
    void selectByCustomization_shouldDropProductWithoutBrandForScopedCaller() {
        CallerScope scope = CallerScope.of(Set.of(3), Set.of(), Set.of());
        when(productCustomizationMapper.selectProductsByCustomizationId(eq(12), eq(List.of(3)), eq(10)))
                .thenReturn(List.of(
                        new CustomizationProductDto().setId(101),
                        new CustomizationProductDto().setId(102).setBrandId(3)));

        CustomizationProductPage page = service.selectByCustomization(12, 10, scope);

        assertEquals(List.of(102), page.getProducts().stream().map(CustomizationProductDto::getId).toList());
    }

    @Test
    void selectByCustomization_shouldNotQueryWhenScopeHasNoBrands() {
        CustomizationProductPage page = service.selectByCustomization(
                12, 10, CallerScope.of(Set.of(), Set.of(), Set.of()));

        assertTrue(page.getProducts().isEmpty());
        verifyNoInteractions(productCustomizationMapper);
    }

    @Test
    void selectByCustomization_shouldRejectNonPositiveId() {
        assertThrows(BusinessBadRequestException.class,
                () -> service.selectByCustomization(0, 10, CallerScope.global()));
        assertThrows(BusinessBadRequestException.class,
                () -> service.selectByCustomization(-1, 10, CallerScope.global()));
        verifyNoInteractions(productCustomizationMapper);
    }
}
