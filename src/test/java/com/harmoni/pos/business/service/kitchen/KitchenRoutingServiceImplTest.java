package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.menu.mapper.CategoryMapper;
import com.harmoni.pos.menu.mapper.KitchenCategoryRoutingMapper;
import com.harmoni.pos.menu.mapper.KitchenProductRoutingMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.KitchenCategoryRouting;
import com.harmoni.pos.menu.model.KitchenProductRouting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenRoutingServiceImplTest {

    @Mock
    private KitchenCategoryRoutingMapper categoryRoutingMapper;
    @Mock
    private KitchenProductRoutingMapper productRoutingMapper;
    @Mock
    private KitchenStationMapper kitchenStationMapper;
    @Mock
    private StoreMapper storeMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private KitchenRoutingServiceImpl service;

    @BeforeEach
    void setUp() {
        when(productRoutingMapper.selectRoutingByStoreId(1)).thenReturn(List.of(
                new KitchenProductRouting().setProductId(10).setKitchenStationId(3)));
        when(categoryRoutingMapper.selectRoutingByStoreId(1)).thenReturn(List.of(
                new KitchenCategoryRouting().setCategoryId(20).setKitchenStationId(4)));
    }

    @Test
    void productRoutingWinsOverCategoryRouting() {
        KitchenRoutingService.RoutingTable table = service.loadRoutingTable(1);

        assertEquals(3, table.resolve(10, 20));
    }

    @Test
    void categoryRoutingAppliesWhenProductHasNoRoute() {
        KitchenRoutingService.RoutingTable table = service.loadRoutingTable(1);

        assertEquals(4, table.resolve(11, 20));
    }

    @Test
    void unassignedWhenNeitherRoutingMatches() {
        KitchenRoutingService.RoutingTable table = service.loadRoutingTable(1);

        assertNull(table.resolve(11, 21));
    }
}
