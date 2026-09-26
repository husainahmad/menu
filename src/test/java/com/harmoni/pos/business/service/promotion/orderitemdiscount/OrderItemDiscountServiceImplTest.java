package com.harmoni.pos.business.service.promotion.orderitemdiscount;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.OrderItemDiscountMapper;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.OrderItemDiscount;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.dto.OrderItemDiscountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemDiscountServiceImplTest {

    @Mock
    private OrderItemDiscountMapper orderItemDiscountMapper;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderItemDiscountServiceImpl orderItemDiscountService;

    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new Promotion().setId(1L).setCode("HAPPY10").setName("Happy Hour");
    }

    private static OrderItemDiscountDto dto(Long orderItemId, Long promotionId, DiscountType type) {
        OrderItemDiscountDto dto = new OrderItemDiscountDto();
        dto.setOrderItemId(orderItemId);
        dto.setPromotionId(promotionId);
        dto.setDiscountType(type);
        dto.setDiscountValue(new BigDecimal("10.00"));
        dto.setDiscountAmount(new BigDecimal("1000.00"));
        return dto;
    }

    @Test
    void create_shouldSnapshotPromotionCodeAndName() {
        when(promotionService.get(1L)).thenReturn(promotion);
        when(orderItemDiscountMapper.insert(any(OrderItemDiscount.class))).thenReturn(1);

        OrderItemDiscount result = orderItemDiscountService
                .create(dto(9001L, 1L, DiscountType.PERCENTAGE));

        assertEquals("HAPPY10", result.getPromotionCode());
        assertEquals("Happy Hour", result.getPromotionName());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void create_shouldAllowManualDiscountWithoutPromotion() {
        when(orderItemDiscountMapper.insert(any(OrderItemDiscount.class))).thenReturn(1);

        OrderItemDiscount result = orderItemDiscountService
                .create(dto(9002L, null, DiscountType.MANUAL));

        assertNull(result.getPromotionCode());
        assertNull(result.getPromotionName());
        assertNotNull(result.getCreatedAt());
        verify(promotionService, never()).get(anyLong());
    }

    @Test
    void create_shouldThrow_whenNonManualDiscountHasNoPromotion() {
        assertThrows(BusinessBadRequestException.class,
                () -> orderItemDiscountService.create(dto(9003L, null, DiscountType.PERCENTAGE)));
        verify(orderItemDiscountMapper, never()).insert(any(OrderItemDiscount.class));
    }

    @Test
    void create_shouldThrow_whenInsertAffectsNoRow() {
        when(promotionService.get(1L)).thenReturn(promotion);
        when(orderItemDiscountMapper.insert(any(OrderItemDiscount.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class,
                () -> orderItemDiscountService.create(dto(9001L, 1L, DiscountType.PERCENTAGE)));
    }

    @Test
    void createBulk_shouldSnapshotEveryRow() {
        when(promotionService.get(1L)).thenReturn(promotion);
        when(orderItemDiscountMapper.insertBatch(anyList())).thenReturn(2);

        List<OrderItemDiscount> result = orderItemDiscountService.createBulk(List.of(
                dto(9001L, 1L, DiscountType.SPECIAL_PRICE),
                dto(9001L, null, DiscountType.MANUAL)));

        assertEquals(2, result.size());
        assertEquals("HAPPY10", result.get(0).getPromotionCode());
        assertNull(result.get(1).getPromotionCode());

        ArgumentCaptor<List<OrderItemDiscount>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderItemDiscountMapper).insertBatch(captor.capture());
        assertNotNull(captor.getValue().get(0).getCreatedAt());
        assertNotNull(captor.getValue().get(1).getCreatedAt());
    }

    @Test
    void createBulk_shouldReturnEmptyList_whenInputEmpty() {
        assertTrue(orderItemDiscountService.createBulk(List.of()).isEmpty());
        verify(orderItemDiscountMapper, never()).insertBatch(anyList());
    }

    @Test
    void createBulk_shouldReturnEmptyList_whenInputNull() {
        assertTrue(orderItemDiscountService.createBulk(null).isEmpty());
        verify(orderItemDiscountMapper, never()).insertBatch(anyList());
    }

    @Test
    void createBulk_shouldPropagateValidationFailure() {
        assertThrows(BusinessBadRequestException.class,
                () -> orderItemDiscountService.createBulk(List.of(dto(9001L, null, DiscountType.FIXED_AMOUNT))));
        verify(orderItemDiscountMapper, never()).insertBatch(anyList());
    }

    @Test
    void getByOrderItemId_shouldDelegate() {
        when(orderItemDiscountMapper.selectByOrderItemId(9001L)).thenReturn(List.of(new OrderItemDiscount()));

        assertEquals(1, orderItemDiscountService.getByOrderItemId(9001L).size());
    }

    @Test
    void getRedemptionSummary_shouldDelegate() {
        Date from = new Date(1_000L);
        Date to = new Date(2_000L);
        when(orderItemDiscountMapper.selectRedemptionSummary(1L, from, to)).thenReturn(List.of());

        assertTrue(orderItemDiscountService.getRedemptionSummary(1L, from, to).isEmpty());
        verify(orderItemDiscountMapper).selectRedemptionSummary(1L, from, to);
    }

    @Test
    void deleteByOrderItemId_shouldDelegate() {
        when(orderItemDiscountMapper.deleteByOrderItemId(9001L)).thenReturn(2);

        assertEquals(2, orderItemDiscountService.deleteByOrderItemId(9001L));
    }
}
