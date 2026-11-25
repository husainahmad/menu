package com.harmoni.pos.business.service.promotion;

import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.PromotionMapper;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.PromotionSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionCatalogServiceImplTest {

    @Mock
    private PromotionMapper promotionMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.PromotionScheduleMapper promotionScheduleMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.PromotionTargetMapper promotionTargetMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.PromotionRuleMapper promotionRuleMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.PromotionSpecialPriceMapper promotionSpecialPriceMapper;

    @Mock
    private com.harmoni.pos.menu.mapper.PromotionScopeMapper promotionScopeMapper;

    @InjectMocks
    private PromotionServiceImpl service;

    @Test
    void listForCatalogItem_shouldReturnSummaries() {
        when(promotionMapper.selectForCatalogItem(anyList(), any(LocalDate.class),
                eq(7L), isNull(), isNull(), isNull(), eq(30)))
                .thenReturn(List.of(new PromotionSummaryDto().setId(1L).setCode("HAPPY10")
                        .setName("Happy Hour").setPromotionType(PromotionType.PERCENTAGE)
                        .setStatus(PromotionStatus.ACTIVE)));

        List<PromotionSummaryDto> result = service.listForCatalogItem(
                7L, null, null, CallerScope.global());

        assertEquals("HAPPY10", result.get(0).getCode());
        assertEquals(PromotionStatus.ACTIVE, result.get(0).getStatus());
        ArgumentCaptor<List<PromotionStatus>> statuses = ArgumentCaptor.forClass(List.class);
        verify(promotionMapper).selectForCatalogItem(statuses.capture(), any(LocalDate.class),
                eq(7L), isNull(), isNull(), isNull(), eq(30));
        assertEquals(List.of(PromotionStatus.ACTIVE, PromotionStatus.SCHEDULED), statuses.getValue());
    }

    @Test
    void listForCatalogItem_shouldPassCallerBrandScope() {
        CallerScope scope = CallerScope.of(Set.of(4), Set.of(), Set.of());
        when(promotionMapper.selectForCatalogItem(anyList(), any(LocalDate.class),
                isNull(), eq(9L), isNull(), eq(List.of(4)), eq(30)))
                .thenReturn(List.of());

        assertTrue(service.listForCatalogItem(null, 9L, null, scope).isEmpty());

        verify(promotionMapper).selectForCatalogItem(anyList(), any(LocalDate.class),
                isNull(), eq(9L), isNull(), eq(List.of(4)), eq(30));
    }

    @Test
    void listForCatalogItem_shouldNotQueryOutsideScope() {
        CallerScope scope = CallerScope.of(Set.of(), Set.of(), Set.of());

        assertTrue(service.listForCatalogItem(null, null, 8L, scope).isEmpty());

        verifyNoInteractions(promotionMapper);
    }

    @Test
    void listForCatalogItem_shouldIgnoreNonPositiveRefs() {
        when(promotionMapper.selectForCatalogItem(anyList(), any(LocalDate.class),
                isNull(), eq(9L), isNull(), isNull(), eq(30)))
                .thenReturn(List.of());

        assertTrue(service.listForCatalogItem(0L, 9L, -1L, CallerScope.global()).isEmpty());

        verify(promotionMapper).selectForCatalogItem(anyList(), any(LocalDate.class),
                isNull(), eq(9L), isNull(), isNull(), eq(30));
    }

    @Test
    void listForCatalogItem_shouldRejectMissingOrInvalidRefs() {
        assertThrows(BusinessBadRequestException.class,
                () -> service.listForCatalogItem(null, null, null, CallerScope.global()));
        assertThrows(BusinessBadRequestException.class,
                () -> service.listForCatalogItem(-1L, null, null, CallerScope.global()));
        verifyNoInteractions(promotionMapper);
    }
}
