package com.harmoni.pos.business.service.service;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.ServiceMapper;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    private ServiceDto serviceDto;

    @BeforeEach
    void setUp() {
        serviceDto = new ServiceDto();
        serviceDto.setName("Test Service");
    }

    @Test
    void create_shouldSucceed() {
        when(serviceMapper.selectByName("Test Service")).thenReturn(null);
        when(serviceMapper.insert(any(com.harmoni.pos.menu.model.Service.class))).thenReturn(1);

        int result = serviceService.create(serviceDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(serviceMapper.selectByName("Test Service")).thenReturn(new com.harmoni.pos.menu.model.Service());

        assertThrows(BusinessBadRequestException.class, () -> serviceService.create(serviceDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(serviceMapper.selectByName("Test Service")).thenReturn(null);
        when(serviceMapper.insert(any(com.harmoni.pos.menu.model.Service.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> serviceService.create(serviceDto));
    }

    @Test
    void getAllWithSub_shouldReturnList() {
        when(serviceMapper.selectAllAndSubService()).thenReturn(List.of(new com.harmoni.pos.menu.model.Service()));

        assertFalse(serviceService.getAllWithSub().isEmpty());
    }
}
