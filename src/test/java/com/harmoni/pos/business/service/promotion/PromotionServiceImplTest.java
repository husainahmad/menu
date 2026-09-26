package com.harmoni.pos.business.service.promotion;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.PromotionMapper;
import com.harmoni.pos.menu.mapper.PromotionRuleMapper;
import com.harmoni.pos.menu.mapper.PromotionScheduleMapper;
import com.harmoni.pos.menu.mapper.PromotionSpecialPriceMapper;
import com.harmoni.pos.menu.mapper.PromotionTargetMapper;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionTargetType;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.PromotionRuleDto;
import com.harmoni.pos.menu.model.dto.PromotionScheduleDto;
import com.harmoni.pos.menu.model.dto.PromotionSpecialPriceDto;
import com.harmoni.pos.menu.model.dto.PromotionTargetDto;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionMapper promotionMapper;

    @Mock
    private PromotionScheduleMapper promotionScheduleMapper;

    @Mock
    private PromotionTargetMapper promotionTargetMapper;

    @Mock
    private PromotionRuleMapper promotionRuleMapper;

    @Mock
    private PromotionSpecialPriceMapper promotionSpecialPriceMapper;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private PromotionAddDto addDto;
    private PromotionEditDto editDto;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        addDto = new PromotionAddDto();
        addDto.setCode("HAPPY10");
        addDto.setName("Happy Hour");
        addDto.setPromotionType(PromotionType.PERCENTAGE);
        addDto.setStatus(PromotionStatus.ACTIVE);
        addDto.setPriority(5);
        addDto.setStartDate(LocalDate.of(2026, 1, 1));
        addDto.setEndDate(LocalDate.of(2026, 12, 31));

        editDto = new PromotionEditDto();
        editDto.setId(1L);
        editDto.setCode("HAPPY10");
        editDto.setName("Happy Hour");
        editDto.setPromotionType(PromotionType.PERCENTAGE);
        editDto.setStatus(PromotionStatus.ACTIVE);

        promotion = new Promotion().setId(1L)
                .setCode("HAPPY10")
                .setName("Happy Hour")
                .setPromotionType(PromotionType.PERCENTAGE)
                .setStatus(PromotionStatus.ACTIVE);

        lenient().when(promotionScheduleMapper.selectByPromotionId(anyLong())).thenReturn(List.of());
        lenient().when(promotionTargetMapper.selectByPromotionId(anyLong())).thenReturn(List.of());
        lenient().when(promotionRuleMapper.selectByPromotionId(anyLong())).thenReturn(List.of());
        lenient().when(promotionSpecialPriceMapper.selectByPromotionId(anyLong())).thenReturn(List.of());
    }

    private static PromotionScheduleDto schedule(DayOfWeek day, String from, String to) {
        PromotionScheduleDto dto = new PromotionScheduleDto();
        dto.setDayOfWeek(day);
        dto.setStartTime(LocalTime.parse(from));
        dto.setEndTime(LocalTime.parse(to));
        return dto;
    }

    private static PromotionTargetDto target(PromotionTargetType type, Long productId, Long skuId, Long categoryId) {
        PromotionTargetDto dto = new PromotionTargetDto();
        dto.setTargetType(type);
        dto.setProductId(productId);
        dto.setSkuId(skuId);
        dto.setCategoryId(categoryId);
        return dto;
    }

    @Test
    void create_shouldSucceed_andStampTimestamps() {
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);
        when(promotionMapper.insert(any(Promotion.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, Promotion.class).setId(1L);
            return 1;
        });

        Promotion result = promotionService.create(addDto);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getSchedules());
    }

    @Test
    void create_shouldDefaultPriorityToZero() {
        addDto.setPriority(null);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);
        when(promotionMapper.insert(any(Promotion.class))).thenReturn(1);

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        promotionService.create(addDto);

        verify(promotionMapper).insert(captor.capture());
        assertEquals(0, captor.getValue().getPriority());
    }

    @Test
    void create_shouldThrow_whenCodeAlreadyExists() {
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.create(addDto));
        verify(promotionMapper, never()).insert(any(Promotion.class));
    }

    @Test
    void create_shouldThrow_whenInsertAffectsNoRow() {
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);
        when(promotionMapper.insert(any(Promotion.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> promotionService.create(addDto));
    }

    @Test
    void create_shouldThrow_whenStartDateAfterEndDate() {
        addDto.setStartDate(LocalDate.of(2026, 12, 31));
        addDto.setEndDate(LocalDate.of(2026, 1, 1));
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.create(addDto));
    }

    @Test
    void create_shouldThrow_whenTargetMissingItsReference() {
        addDto.setTargetDtos(List.of(target(PromotionTargetType.SKU, null, null, null)));
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.create(addDto));
    }

    @Test
    void create_shouldBatchInsertEveryChildCollection() {
        addDto.setScheduleDtos(List.of(
                schedule(DayOfWeek.MONDAY, "15:00", "17:00"),
                schedule(DayOfWeek.FRIDAY, "23:00", "01:00")));
        addDto.setTargetDtos(List.of(
                target(PromotionTargetType.CATEGORY, null, null, 13L),
                target(PromotionTargetType.SKU, null, 1L, null)));
        PromotionRuleDto rule = new PromotionRuleDto();
        rule.setDiscountValue(new BigDecimal("10.00"));
        addDto.setRuleDtos(List.of(rule));
        PromotionSpecialPriceDto specialPrice = new PromotionSpecialPriceDto();
        specialPrice.setSkuId(1L);
        specialPrice.setSpecialPrice(new BigDecimal("9000.00"));
        addDto.setSpecialPriceDtos(List.of(specialPrice));

        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);
        when(promotionMapper.insert(any(Promotion.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, Promotion.class).setId(7L);
            return 1;
        });

        promotionService.create(addDto);

        verify(promotionScheduleMapper).insertBatch(argThat(rows -> rows.size() == 2
                && rows.get(0).getPromotionId() == 7L
                && rows.get(0).getDayOfWeek() == DayOfWeek.MONDAY));
        verify(promotionTargetMapper).insertBatch(argThat(rows -> rows.size() == 2
                && rows.get(0).getPromotionId() == 7L
                && rows.get(0).getTargetType() == PromotionTargetType.CATEGORY));
        verify(promotionRuleMapper).insertBatch(argThat(rows -> rows.size() == 1
                && rows.get(0).getPromotionId() == 7L));
        verify(promotionSpecialPriceMapper).insertOrUpdateBatch(argThat(rows -> rows.size() == 1
                && rows.get(0).getPromotionId() == 7L));
    }

    @Test
    void create_shouldSkipChildInsert_whenCollectionsEmpty() {
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(null);
        when(promotionMapper.insert(any(Promotion.class))).thenReturn(1);

        promotionService.create(addDto);

        verify(promotionScheduleMapper, never()).insertBatch(anyList());
        verify(promotionTargetMapper, never()).insertBatch(anyList());
        verify(promotionRuleMapper, never()).insertBatch(anyList());
        verify(promotionSpecialPriceMapper, never()).insertOrUpdateBatch(anyList());
    }

    @Test
    void update_shouldReplaceEveryChild_whenAllCollectionsSubmitted() {
        editDto.setScheduleDtos(List.of(schedule(DayOfWeek.TUESDAY, "10:00", "12:00")));
        editDto.setTargetDtos(List.of(target(PromotionTargetType.PRODUCT, 1L, null, null)));
        PromotionRuleDto rule = new PromotionRuleDto();
        editDto.setRuleDtos(List.of(rule));
        PromotionSpecialPriceDto specialPrice = new PromotionSpecialPriceDto();
        specialPrice.setSkuId(1L);
        editDto.setSpecialPriceDtos(List.of(specialPrice));

        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);
        when(promotionMapper.updateByPrimaryKey(any(Promotion.class))).thenReturn(1);

        promotionService.update(editDto);

        verify(promotionScheduleMapper).deleteByPromotionId(1L);
        verify(promotionTargetMapper).deleteByPromotionId(1L);
        verify(promotionRuleMapper).deleteByPromotionId(1L);
        verify(promotionSpecialPriceMapper).deleteByPromotionId(1L);
        verify(promotionScheduleMapper).insertBatch(anyList());
    }

    @Test
    void update_shouldPreserveChildren_whenCollectionsOmitted() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);
        when(promotionMapper.updateByPrimaryKey(any(Promotion.class))).thenReturn(1);

        promotionService.update(editDto);

        verify(promotionScheduleMapper, never()).deleteByPromotionId(anyLong());
        verify(promotionTargetMapper, never()).deleteByPromotionId(anyLong());
        verify(promotionRuleMapper, never()).deleteByPromotionId(anyLong());
        verify(promotionSpecialPriceMapper, never()).deleteByPromotionId(anyLong());
        verify(promotionScheduleMapper, never()).insertBatch(anyList());
    }

    @Test
    void update_shouldClearChildren_whenCollectionSubmittedEmpty() {
        editDto.setTargetDtos(List.of());
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);
        when(promotionMapper.updateByPrimaryKey(any(Promotion.class))).thenReturn(1);

        promotionService.update(editDto);

        verify(promotionTargetMapper).deleteByPromotionId(1L);
        verify(promotionScheduleMapper, never()).deleteByPromotionId(anyLong());
    }

    @Test
    void update_shouldThrow_whenCodeBelongsToAnotherPromotion() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(new Promotion().setId(99L));

        assertThrows(BusinessBadRequestException.class, () -> promotionService.update(editDto));
    }

    @Test
    void update_shouldThrow_whenPromotionAbsent() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.update(editDto));
    }

    @Test
    void update_shouldPreserveCreatedAt() {
        promotion.setCreatedAt(new java.util.Date(1_000L));
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);
        when(promotionMapper.updateByPrimaryKey(any(Promotion.class))).thenReturn(1);

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        promotionService.update(editDto);

        verify(promotionMapper).updateByPrimaryKey(captor.capture());
        assertEquals(1_000L, captor.getValue().getCreatedAt().getTime());
    }

    @Test
    void delete_shouldCascadeChildren() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.deleteByPrimaryKey(1L)).thenReturn(1);

        assertEquals(1, promotionService.delete(1L));

        InOrder inOrder = inOrder(promotionScheduleMapper, promotionTargetMapper,
                promotionRuleMapper, promotionSpecialPriceMapper, promotionMapper);
        inOrder.verify(promotionScheduleMapper).deleteByPromotionId(1L);
        inOrder.verify(promotionTargetMapper).deleteByPromotionId(1L);
        inOrder.verify(promotionRuleMapper).deleteByPromotionId(1L);
        inOrder.verify(promotionSpecialPriceMapper).deleteByPromotionId(1L);
        inOrder.verify(promotionMapper).deleteByPrimaryKey(1L);
    }

    @Test
    void delete_shouldThrow_whenPromotionAbsent() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.delete(1L));
    }

    @Test
    void deleteByFilter_shouldCascadeChildrenForEveryMatch() {
        when(promotionMapper.selectByFilter(PromotionStatus.EXPIRED, null, "old"))
                .thenReturn(List.of(promotion, new Promotion().setId(2L)));
        when(promotionMapper.deleteByFilter(PromotionStatus.EXPIRED, null, "old")).thenReturn(2);

        assertEquals(2, promotionService.deleteByFilter(PromotionStatus.EXPIRED, null, "old"));

        verify(promotionScheduleMapper).deleteByPromotionId(1L);
        verify(promotionScheduleMapper).deleteByPromotionId(2L);
    }

    @Test
    void get_shouldAttachEveryChildCollection() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionScheduleMapper.selectByPromotionId(1L)).thenReturn(List.of());
        when(promotionTargetMapper.selectByPromotionId(1L))
                .thenReturn(List.of(new com.harmoni.pos.menu.model.PromotionTarget().setId(3L)));
        when(promotionRuleMapper.selectByPromotionId(1L)).thenReturn(List.of());
        when(promotionSpecialPriceMapper.selectByPromotionId(1L)).thenReturn(List.of());

        Promotion result = promotionService.get(1L);

        assertNotNull(result.getSchedules());
        assertNotNull(result.getTargets());
        assertNotNull(result.getRules());
        assertNotNull(result.getSpecialPrices());
        assertEquals(1, result.getTargets().size());
    }

    @Test
    void get_shouldThrow_whenPromotionAbsent() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.get(1L));
    }

    @Test
    void getByCode_shouldReturnPromotion() {
        when(promotionMapper.selectByCode("HAPPY10")).thenReturn(promotion);

        assertEquals("HAPPY10", promotionService.getByCode("HAPPY10").getCode());
    }

    @Test
    void getByCode_shouldThrow_whenAbsent() {
        when(promotionMapper.selectByCode("NOPE")).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> promotionService.getByCode("NOPE"));
    }

    @Test
    void listPaginated_shouldReturnPageEnvelope() {
        when(promotionMapper.selectByFilter(PromotionStatus.ACTIVE, PromotionType.PERCENTAGE, "happy"))
                .thenReturn(List.of(promotion));

        Map<String, Object> page = promotionService
                .listPaginated(PromotionStatus.ACTIVE, PromotionType.PERCENTAGE, "happy", 1, 10);

        assertTrue(page.containsKey("page"));
        assertTrue(page.containsKey("size"));
        assertTrue(page.containsKey("total"));
        assertTrue(page.containsKey("data"));
        assertTrue(page.containsKey("navigate"));
    }

    @Test
    void listPaginated_shouldNormaliseBlankSearchToNull() {
        when(promotionMapper.selectByFilter(null, null, null)).thenReturn(List.of());

        promotionService.listPaginated(null, null, "   ", 1, 10);

        verify(promotionMapper).selectByFilter(null, null, null);
    }

    @Test
    void listRedeemable_shouldQueryActiveAndScheduledOnToday() {
        when(promotionMapper.selectRedeemableOn(anyList(), any(LocalDate.class)))
                .thenReturn(List.of(promotion));

        assertEquals(1, promotionService.listRedeemable().size());

        ArgumentCaptor<List<PromotionStatus>> captor = ArgumentCaptor.forClass(List.class);
        verify(promotionMapper).selectRedeemableOn(captor.capture(), any(LocalDate.class));
        assertEquals(List.of(PromotionStatus.ACTIVE, PromotionStatus.SCHEDULED), captor.getValue());
    }

    @Test
    void updateStatus_shouldDelegate() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(promotion);
        when(promotionMapper.updateStatus(1L, PromotionStatus.PAUSED)).thenReturn(1);

        assertEquals(1, promotionService.updateStatus(1L, PromotionStatus.PAUSED));
    }

    @Test
    void updateStatus_shouldThrow_whenPromotionAbsent() {
        when(promotionMapper.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class,
                () -> promotionService.updateStatus(1L, PromotionStatus.PAUSED));
    }
}
