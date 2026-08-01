package com.harmoni.pos.http.controller.storeservicetype;

import com.harmoni.pos.business.service.storeservicetype.StoreServiceTypeService;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTypeControllerTest {

    @Mock
    private StoreServiceTypeService storeServiceTypeService;

    @InjectMocks
    private StoreServiceTypeController storeServiceTypeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(storeServiceTypeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        StoreServiceTypeDto dto = new StoreServiceTypeDto();
        dto.setStoreId(1);
        dto.setSubServiceId(1);
        when(storeServiceTypeService.create(any(StoreServiceTypeDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/storeservicetype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
