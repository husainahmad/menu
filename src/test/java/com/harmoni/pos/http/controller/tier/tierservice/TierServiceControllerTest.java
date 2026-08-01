package com.harmoni.pos.http.controller.tier.tierservice;

import com.harmoni.pos.business.service.tier.tierservice.TierServiceService;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierServiceDto;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TierServiceControllerTest {

    @Mock
    private TierServiceService tierServiceService;

    @InjectMocks
    private TierServiceController tierServiceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tierServiceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        TierServiceDto dto = new TierServiceDto();
        TierEditDto tierDto = new TierEditDto();
        tierDto.setId(1);
        tierDto.setName("Gold");
        tierDto.setBrandId(1);
        tierDto.setType(TierType.PRICE);
        dto.setTierDto(tierDto);
        SubServiceDto subServiceDto = new SubServiceDto();
        subServiceDto.setId(1);
        subServiceDto.setServiceId(1);
        subServiceDto.setName("Sub Name");
        dto.setSubServiceDto(subServiceDto);
        dto.setActive(true);
        when(tierServiceService.create(any(TierServiceDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/tier/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getByBrand_shouldReturn200() throws Exception {
        when(tierServiceService.getByBrandId(1)).thenReturn(List.of(new TierService().setId(1)));

        mockMvc.perform(get("/api/v1/tier/service")
                        .param("brandId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        TierServiceDto dto = new TierServiceDto();
        TierEditDto tierDto = new TierEditDto();
        tierDto.setId(1);
        tierDto.setName("Gold");
        tierDto.setBrandId(1);
        tierDto.setType(TierType.PRICE);
        dto.setTierDto(tierDto);
        SubServiceDto subServiceDto = new SubServiceDto();
        subServiceDto.setId(1);
        subServiceDto.setServiceId(1);
        subServiceDto.setName("Sub Name");
        dto.setSubServiceDto(subServiceDto);
        dto.setActive(true);
        when(tierServiceService.updateTierServices(anyList(), eq(1))).thenReturn(1);

        mockMvc.perform(put("/api/v1/tier/1/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isOk());
    }
}
