package com.harmoni.pos.http.controller.product;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.business.service.product.CustomizationCatalogService;
import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.business.service.product.ProductService;
import com.harmoni.pos.business.service.product.ProductSkuService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.handler.BadRequestExceptionHandler;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductDto;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductPage;
import com.harmoni.pos.menu.model.dto.product.CustomizationProductSkuDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductByCustomizationControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductSkuService productSkuService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CustomizationCatalogService customizationCatalogService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new BadRequestExceptionHandler(messageSource))
                .build();
    }

    @Test
    void byCustomization_shouldReturnWrappedPage() throws Exception {
        CustomizationProductPage page = new CustomizationProductPage().setProducts(List.of(
                new CustomizationProductDto().setId(101).setName("Burger").setCategoryId(7)
                        .setSkus(List.of(new CustomizationProductSkuDto().setId(9).setName("Large")))));
        when(customizationCatalogService.selectByCustomization(eq(12), eq(10), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/product/by-customization/12").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.products[0].id").value(101))
                .andExpect(jsonPath("$.data.products[0].skus[0].name").value("Large"))
                .andExpect(jsonPath("$.data.nextCursor").doesNotExist())
                .andExpect(jsonPath("$.data.hasMore").value(false));
    }

    @Test
    void byCustomization_shouldReturnEmptyPageForScopeWithoutBrands() throws Exception {
        CallerScopeContext.set(CallerScope.none());
        try {
            when(customizationCatalogService.selectByCustomization(eq(12), isNull(),
                    argThat(scope -> !scope.isGlobal() && scope.getBrandIds().isEmpty())))
                    .thenReturn(new CustomizationProductPage());

            mockMvc.perform(get("/api/v1/product/by-customization/12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.products").isEmpty());
        } finally {
            CallerScopeContext.clear();
        }
    }

    @Test
    void byCustomization_shouldReturnBadRequestForInvalidId() throws Exception {
        when(customizationCatalogService.selectByCustomization(eq(0), isNull(), any()))
                .thenThrow(new BusinessBadRequestException("exception.customization.id.badRequest.invalid", null));

        mockMvc.perform(get("/api/v1/product/by-customization/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Bad Request, customization id must be positive"));
    }
}
