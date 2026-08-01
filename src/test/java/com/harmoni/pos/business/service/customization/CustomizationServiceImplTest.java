package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.business.service.customizationoption.CustomizationOptionService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.menu.mapper.CustomizationMapper;
import com.harmoni.pos.menu.model.*;
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
class CustomizationServiceImplTest {

    @Mock
    private CustomizationMapper customizationMapper;

    @Mock
    private UserService userService;

    @Mock
    private CustomizationOptionService customizationOptionService;

    @InjectMocks
    private CustomizationServiceImpl customizationService;

    private Customization customization;

    @BeforeEach
    void setUp() {
        customization = new Customization()
                .setId(1)
                .setName("Extra Cheese")
                .setSelectionType(SelectionType.SINGLE)
                .setBrandId(1)
                .setCustomizationOptions(List.of(new CustomizationOption().setName("Cheddar")));
    }

    @Test
    void getCustomizationById_shouldReturn() {
        when(customizationMapper.selectByPrimaryKey(1)).thenReturn(customization);
        assertTrue(customizationService.getCustomizationById(1).isPresent());
    }

    @Test
    void getCustomizationById_shouldReturnEmpty() {
        when(customizationMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertTrue(customizationService.getCustomizationById(1).isEmpty());
    }

    @Test
    void getCustomizationsByBrandId_shouldReturnList() {
        when(customizationMapper.selectByBrandId(1)).thenReturn(List.of(customization));
        assertFalse(customizationService.getCustomizationsByBrandId(1).isEmpty());
    }

    @Test
    void createCustomization_shouldSucceed() {
        Brand brand = new Brand().setId(1);
        Chain chain = new Chain().setBrandId(1).setBrand(brand);
        Store store = new Store().setId(1).setChain(chain);
        User user = new User().setId(1).setStoreId(1).setStore(store);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(customizationMapper.insert(customization)).thenReturn(1);
        when(customizationOptionService.createBulk(anyList(), eq(1))).thenReturn(1);

        int result = customizationService.createCustomization("Bearer token123", customization);
        assertEquals(1, result);
        assertEquals(1, customization.getBrandId());
    }

    @Test
    void updateCustomization_shouldReturnRows() {
        when(customizationMapper.updateByPrimaryKey(customization)).thenReturn(1);
        assertEquals(1, customizationService.updateCustomization(customization));
    }

    @Test
    void deleteCustomization_shouldReturnRows() {
        when(customizationMapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, customizationService.deleteCustomization(1));
    }
}
