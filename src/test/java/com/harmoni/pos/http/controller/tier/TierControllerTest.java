package com.harmoni.pos.http.controller.tier;

import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;
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
class TierControllerTest {

    @Mock
    private TierService tierService;

    @InjectMocks
    private TierController tierController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tierController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        TierDto dto = new TierDto();
        dto.setName("Gold");
        dto.setBrandId(1);
        dto.setType(TierType.PRICE);
        when(tierService.create(any(TierDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/tier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(tierService.get(1)).thenReturn(new Tier().setId(1).setName("Gold"));

        mockMvc.perform(get("/api/v1/tier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Gold"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        TierDto dto = new TierDto();
        dto.setName("Gold");
        dto.setBrandId(1);
        dto.setType(TierType.PRICE);
        when(tierService.update(any(TierDto.class), eq(1))).thenReturn(true);

        mockMvc.perform(put("/api/v1/tier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getByBrand_shouldReturn200() throws Exception {
        when(tierService.getByBrandId(1)).thenReturn(List.of(new Tier().setId(1).setName("Gold")));

        mockMvc.perform(get("/api/v1/tier/brand/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByBrand_shouldReturn200() throws Exception {
        when(tierService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/tier/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getByBrandType_shouldReturn200() throws Exception {
        when(tierService.getByBrandIdAndTierType(1, TierType.PRICE)).thenReturn(List.of(new Tier().setId(1)));

        mockMvc.perform(get("/api/v1/tier/brand/1/type/PRICE"))
                .andExpect(status().isOk());
    }
}
