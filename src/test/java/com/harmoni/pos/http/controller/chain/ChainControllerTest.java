package com.harmoni.pos.http.controller.chain;

import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;
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
class ChainControllerTest {

    @Mock
    private ChainService chainService;

    @InjectMocks
    private ChainController chainController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chainController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        ChainDto dto = new ChainDto();
        dto.setName("Chain1");
        dto.setBrandId(1);
        when(chainService.create(any(ChainDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/chain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void update_shouldReturn202() throws Exception {
        ChainDto dto = new ChainDto();
        dto.setName("Chain1");
        dto.setBrandId(1);
        when(chainService.update(any(ChainDto.class), eq(1))).thenReturn(true);

        mockMvc.perform(put("/api/v1/chain/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(chainService.get(1)).thenReturn(new Chain().setId(1).setName("Chain1"));

        mockMvc.perform(get("/api/v1/chain/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Chain1"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(chainService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/chain/1"))
                .andExpect(status().isOk());
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(chainService.list()).thenReturn(List.of(new Chain().setId(1)));

        mockMvc.perform(get("/api/v1/chain"))
                .andExpect(status().isOk());
    }

    @Test
    void listByBrandId_shouldReturn200() throws Exception {
        when(chainService.listByBrandId(1)).thenReturn(List.of(new Chain().setId(1)));

        mockMvc.perform(get("/api/v1/chain/brand/1"))
                .andExpect(status().isOk());
    }
}
