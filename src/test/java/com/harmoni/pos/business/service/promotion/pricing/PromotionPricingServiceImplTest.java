package com.harmoni.pos.business.service.promotion.pricing;

import com.harmoni.pos.business.service.promotion.engine.AppliedDiscount;
import com.harmoni.pos.business.service.promotion.engine.CartLine;
import com.harmoni.pos.business.service.promotion.engine.DiscountOutcome;
import com.harmoni.pos.business.service.promotion.engine.PromotionContext;
import com.harmoni.pos.business.service.promotion.engine.PromotionEngine;
import com.harmoni.pos.business.service.promotion.engine.TargetMatch;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.dto.pricing.CartLineDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionContextDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionPricingServiceImplTest {

    private static final Long STORE = 7L;
    private static final ZoneId SYDNEY = ZoneId.of("Australia/Sydney");

    @Mock
    private PromotionEngine promotionEngine;

    private PromotionPricingServiceImpl pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PromotionPricingServiceImpl(promotionEngine);
    }

    private static CartLineDto line(int index, String unitPrice, String quantity) {
        CartLineDto dto = new CartLineDto();
        dto.setLineIndex(index);
        dto.setProductId(10L);
        dto.setSkuId(20L);
        dto.setCategoryId(30L);
        dto.setUnitPrice(new BigDecimal(unitPrice));
        dto.setQuantity(new BigDecimal(quantity));
        return dto;
    }

    private static PromotionPriceRequestDto request(List<CartLineDto> lines) {
        PromotionContextDto context = new PromotionContextDto();
        context.setStoreId(STORE);
        context.setZone(SYDNEY.getId());

        PromotionPriceRequestDto request = new PromotionPriceRequestDto();
        request.setLines(lines);
        request.setContext(context);
        return request;
    }

    @Test
    void price_shouldEchoEveryLineBackInRequestOrder() {
        when(promotionEngine.evaluate(anyList(), any())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"),
                        List.of(applied("2.00"))),
                new DiscountOutcome(null, new BigDecimal("50.00"),
                        List.of(applied("5.00")))));

        PromotionPriceResponseDto result = pricingService.price(request(List.of(
                line(0, "20.00", "1"), line(1, "25.00", "2"))));

        assertEquals(2, result.getLines().size());
        assertEquals(0, result.getLines().get(0).getLineIndex());
        assertEquals(1, result.getLines().get(1).getLineIndex());
        assertEquals(new BigDecimal("20.00"), result.getLines().get(0).getGrossAmount());
        assertEquals(new BigDecimal("50.00"), result.getLines().get(1).getGrossAmount());
    }

    @Test
    void price_shouldTotalDiscountAndNetAcrossLines() {
        when(promotionEngine.evaluate(anyList(), any())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of(applied("2.00"))),
                new DiscountOutcome(null, new BigDecimal("50.00"), List.of(applied("5.00")))));

        PromotionPriceResponseDto result = pricingService.price(request(List.of(
                line(0, "20.00", "1"), line(1, "25.00", "2"))));

        assertEquals(new BigDecimal("7.00"), result.getTotalDiscount());
        assertEquals(new BigDecimal("63.00"), result.getTotalNet());
    }

    @Test
    void price_shouldReportAnUndiscountedLineAsSuch() {
        when(promotionEngine.evaluate(anyList(), any())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of())));

        PromotionPriceResponseDto result = pricingService.price(request(List.of(line(0, "20.00", "1"))));

        assertFalse(result.getLines().get(0).isDiscounted());
        assertTrue(result.getLines().get(0).getDiscounts().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotalDiscount());
        assertEquals(new BigDecimal("20.00"), result.getTotalNet());
    }

    @Test
    void price_shouldCarryPromotionCodeAndNameForTheAuditTrail() {
        when(promotionEngine.evaluate(anyList(), any())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of(applied("2.00")))));

        PromotionPriceResponseDto result = pricingService.price(request(List.of(line(0, "20.00", "1"))));

        var discount = result.getLines().get(0).getDiscounts().get(0);
        assertEquals(99L, discount.getPromotionId());
        assertEquals("HAPPY10", discount.getPromotionCode());
        assertEquals("Happy Hour", discount.getPromotionName());
        assertEquals(DiscountType.PERCENTAGE, discount.getDiscountType());
        assertEquals(TargetMatch.SKU, discount.getTargetMatch());
        assertEquals(new BigDecimal("2.00"), discount.getDiscountAmount());
    }

    @Test
    void price_shouldLeaveTheOrderItemIdUnsetBecauseNothingIsPersistedYet() {
        ArgumentCaptor<List<CartLine>> captor = ArgumentCaptor.forClass(List.class);
        when(promotionEngine.evaluate(captor.capture(), any())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of())));

        pricingService.price(request(List.of(line(0, "20.00", "1"))));

        assertEquals(1, captor.getValue().size());
        assertEquals(null, captor.getValue().get(0).orderItemId());
    }

    @Test
    void price_shouldPassTheStoreAndZoneThroughToTheEngine() {
        ArgumentCaptor<PromotionContext> captor = ArgumentCaptor.forClass(PromotionContext.class);
        when(promotionEngine.evaluate(anyList(), captor.capture())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of())));

        pricingService.price(request(List.of(line(0, "20.00", "1"))));

        assertEquals(STORE, captor.getValue().storeId());
        assertEquals(SYDNEY, captor.getValue().zone());
    }

    @Test
    void price_whenZoneAbsent_shouldFallBackToTheSystemDefault() {
        ArgumentCaptor<PromotionContext> captor = ArgumentCaptor.forClass(PromotionContext.class);
        when(promotionEngine.evaluate(anyList(), captor.capture())).thenReturn(List.of(
                new DiscountOutcome(null, new BigDecimal("20.00"), List.of())));

        PromotionPriceRequestDto request = request(List.of(line(0, "20.00", "1")));
        request.getContext().setZone(null);

        pricingService.price(request);

        assertEquals(ZoneId.systemDefault(), captor.getValue().zone());
    }

    @Test
    void price_whenNoLines_shouldReject() {
        assertThrows(BusinessBadRequestException.class, () -> pricingService.price(request(List.of())));
        verify(promotionEngine, never()).evaluate(anyList(), any());
    }

    @Test
    void price_whenRequestNull_shouldReject() {
        assertThrows(BusinessBadRequestException.class, () -> pricingService.price(null));
    }

    @Test
    void price_whenContextMissing_shouldReject() {
        PromotionPriceRequestDto request = new PromotionPriceRequestDto();
        request.setLines(List.of(line(0, "20.00", "1")));

        assertThrows(BusinessBadRequestException.class, () -> pricingService.price(request));
        verify(promotionEngine, never()).evaluate(anyList(), any());
    }

    private static AppliedDiscount applied(String amount) {
        return new AppliedDiscount(null, 99L, "HAPPY10", "Happy Hour", DiscountType.PERCENTAGE,
                new BigDecimal("10.00"), new BigDecimal(amount), TargetMatch.SKU);
    }
}
