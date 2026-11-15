package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.KitchenPrinterMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.model.KitchenPrinter;
import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenPrinterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenPrinterServiceImplTest {

    @Mock
    private KitchenPrinterMapper kitchenPrinterMapper;
    @Mock
    private KitchenStationMapper kitchenStationMapper;
    @InjectMocks
    private KitchenPrinterServiceImpl service;

    private KitchenStation station;
    private KitchenPrinterDto dto;

    @BeforeEach
    void setUp() {
        station = new KitchenStation().setId(1).setStoreId(2).setName("Grill").setStatus("ACTIVE");
        dto = new KitchenPrinterDto();
        dto.setName("Grill Printer");
        dto.setPrinterType("NETWORK");
        dto.setConnectionConfig("{\"host\":\"127.0.0.1\",\"port\":9100}");
        dto.setPaperWidth("80MM");
    }

    @Test
    void firstPrinterBecomesPrimary() {
        when(kitchenStationMapper.selectByPrimaryKey(1)).thenReturn(station);
        when(kitchenPrinterMapper.selectByStationId(1)).thenReturn(List.of());
        when(kitchenPrinterMapper.insert(any())).thenReturn(1);

        KitchenPrinter created = service.create(1, dto);

        assertTrue(created.getIsPrimary());
    }

    @Test
    void creatingSecondPrimaryDemotesTheFirst() {
        when(kitchenStationMapper.selectByPrimaryKey(1)).thenReturn(station);
        when(kitchenPrinterMapper.selectByStationId(1)).thenReturn(List.of(
                new KitchenPrinter().setId(5).setName("Old").setIsPrimary(true)));
        when(kitchenPrinterMapper.insert(any())).thenReturn(1);
        dto.setIsPrimary(true);

        service.create(1, dto);

        verify(kitchenPrinterMapper).clearPrimaryByStationId(1, null);
    }

    @Test
    void duplicatePrinterNameIsRejected() {
        when(kitchenStationMapper.selectByPrimaryKey(1)).thenReturn(station);
        when(kitchenPrinterMapper.selectByStationIdAndName(1, "Grill Printer"))
                .thenReturn(new KitchenPrinter().setId(9));

        assertThrows(BusinessBadRequestException.class, () -> service.create(1, dto));
        verify(kitchenPrinterMapper, never()).insert(any());
    }

}
