package com.harmoni.pos.business.service.product;

import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.model.ProductCustomization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationServiceImplTest {

    @Mock
    private ProductCustomizationMapper mapper;

    @InjectMocks
    private ProductCustomizationServiceImpl productCustomizationService;

    private ProductCustomization pc;

    @BeforeEach
    void setUp() {
        pc = new ProductCustomization().setId(1).setProductId(10).setCustomizationId(20);
    }

    @Test
    void getById_shouldReturn() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        assertTrue(productCustomizationService.getById(1).isPresent());
    }

    @Test
    void getById_shouldReturnEmpty() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(null);
        assertTrue(productCustomizationService.getById(1).isEmpty());
    }

    @Test
    void getByProductId_shouldReturnList() {
        when(mapper.selectByProductId(10)).thenReturn(List.of(pc));
        assertFalse(productCustomizationService.getByProductId(10).isEmpty());
    }

    @Test
    void create_shouldReturnRows() {
        when(mapper.insert(pc)).thenReturn(1);
        assertEquals(1, productCustomizationService.create(pc));
    }

    @Test
    void update_shouldReturnRows() {
        when(mapper.updateByPrimaryKey(pc)).thenReturn(1);
        assertEquals(1, productCustomizationService.update(pc));
    }

    @Test
    void delete_shouldReturnRows() {
        when(mapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, productCustomizationService.delete(1));
    }
}
