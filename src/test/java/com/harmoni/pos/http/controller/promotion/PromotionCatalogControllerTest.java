package com.harmoni.pos.http.controller.promotion;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.business.service.promotion.pricing.PromotionPricingService;
import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.handler.BadRequestExceptionHandler;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.PromotionSummaryDto;
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
class PromotionCatalogControllerTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private PromotionPricingService promotionPricingService;

    @InjectMocks
    private PromotionController promotionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        mockMvc = MockMvcBuilders.standaloneSetup(promotionController)
                .setControllerAdvice(new BadRequestExceptionHandler(messageSource))
                .build();
    }

    @Test
    void forCatalogItem_shouldReturnWrappedArray() throws Exception {
        when(promotionService.listForCatalogItem(eq(7L), isNull(), isNull(), any()))
                .thenReturn(List.of(new PromotionSummaryDto().setId(1L).setCode("HAPPY10")
                        .setName("Happy Hour").setPromotionType(PromotionType.PERCENTAGE)
                        .setStatus(PromotionStatus.ACTIVE)));

        mockMvc.perform(get("/api/v1/promotion/for-catalog-item").param("productId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("HAPPY10"))
                .andExpect(jsonPath("$.data[0].promotionType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    @Test
    void forCatalogItem_shouldReturnEmptyArrayForScopeWithoutBrands() throws Exception {
        CallerScopeContext.set(CallerScope.none());
        try {
            when(promotionService.listForCatalogItem(eq(7L), isNull(), isNull(),
                    argThat(scope -> !scope.isGlobal() && scope.getBrandIds().isEmpty())))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/v1/promotion/for-catalog-item").param("productId", "7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        } finally {
            CallerScopeContext.clear();
        }
    }

    @Test
    void forCatalogItem_shouldReturnBadRequestWhenNoRefGiven() throws Exception {
        when(promotionService.listForCatalogItem(isNull(), isNull(), isNull(), any()))
                .thenThrow(new BusinessBadRequestException("exception.promotion.catalog.badRequest.invalid", null));

        mockMvc.perform(get("/api/v1/promotion/for-catalog-item"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Bad Request, at least one positive productId, skuId or categoryId is required"));
    }
}
