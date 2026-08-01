package com.harmoni.pos.http.controller.service;

import com.harmoni.pos.business.service.service.ServiceService;
import com.harmoni.pos.menu.model.Service;
import com.harmoni.pos.menu.model.dto.ServiceDto;
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
class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(serviceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        ServiceDto dto = new ServiceDto();
        dto.setName("Delivery");
        when(serviceService.create(any(ServiceDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(serviceService.getAllWithSub()).thenReturn(List.of(new Service().setId(1).setName("Delivery")));

        mockMvc.perform(get("/api/v1/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Delivery"));
    }
}
