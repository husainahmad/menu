package com.harmoni.pos.business.service.subservice;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SubServiceMapper;
import com.harmoni.pos.menu.model.SubService;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubServiceServiceImplTest {

    @Mock
    private SubServiceMapper subServiceMapper;

    @InjectMocks
    private SubServiceServiceImpl subServiceService;

    private SubServiceDto subServiceDto;

    @BeforeEach
    void setUp() {
        subServiceDto = new SubServiceDto();
        subServiceDto.setName("Test SubService");
        subServiceDto.setServiceId(1);
    }

    @Test
    void create_shouldSucceed() {
        when(subServiceMapper.selectByNameServiceId("Test SubService", 1)).thenReturn(null);
        when(subServiceMapper.insert(any(SubService.class))).thenReturn(1);

        int result = subServiceService.create(subServiceDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(subServiceMapper.selectByNameServiceId("Test SubService", 1)).thenReturn(new SubService());

        assertThrows(BusinessBadRequestException.class, () -> subServiceService.create(subServiceDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(subServiceMapper.selectByNameServiceId("Test SubService", 1)).thenReturn(null);
        when(subServiceMapper.insert(any(SubService.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> subServiceService.create(subServiceDto));
    }
}
