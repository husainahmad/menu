package com.harmoni.pos.business.service.customizationoption;

import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
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
class CustomizationOptionServiceImplTest {

    @Mock
    private CustomizationOptionMapper optionMapper;

    @InjectMocks
    private CustomizationOptionServiceImpl customizationOptionService;

    private CustomizationOption option;

    @BeforeEach
    void setUp() {
        option = new CustomizationOption().setId(1).setName("Option 1").setCustomizationId(10);
    }

    @Test
    void getById_shouldReturnOption() {
        when(optionMapper.selectByPrimaryKey(1)).thenReturn(option);
        Optional<CustomizationOption> result = customizationOptionService.getById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void getById_shouldReturnEmpty_whenNotFound() {
        when(optionMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertTrue(customizationOptionService.getById(1).isEmpty());
    }

    @Test
    void getByCustomizationId_shouldReturnList() {
        when(optionMapper.selectByCustomizationId(10)).thenReturn(List.of(option));
        assertFalse(customizationOptionService.getByCustomizationId(10).isEmpty());
    }

    @Test
    void create_shouldReturnTrue() {
        when(optionMapper.insert(option)).thenReturn(1);
        assertTrue(customizationOptionService.create(option));
    }

    @Test
    void create_shouldReturnFalse() {
        when(optionMapper.insert(option)).thenReturn(0);
        assertFalse(customizationOptionService.create(option));
    }

    @Test
    void createBulk_shouldReturnRows() {
        when(optionMapper.insertOrUpdateBulk(List.of(option), 10)).thenReturn(1);
        assertEquals(1, customizationOptionService.createBulk(List.of(option), 10));
    }

    @Test
    void update_shouldReturnTrue() {
        when(optionMapper.updateByPrimaryKey(option)).thenReturn(1);
        assertTrue(customizationOptionService.update(option));
    }

    @Test
    void delete_shouldReturnTrue() {
        when(optionMapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertTrue(customizationOptionService.delete(1));
    }
}
