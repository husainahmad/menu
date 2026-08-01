package com.harmoni.pos.http.controller.subservice;

import com.harmoni.pos.business.service.subservice.SubServiceService;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
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
class SubServiceControllerTest {

    @Mock
    private SubServiceService subServiceService;

    @InjectMocks
    private SubServiceController subServiceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subServiceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        SubServiceDto dto = new SubServiceDto();
        dto.setName("Sub1");
        dto.setServiceId(1);
        when(subServiceService.create(any(SubServiceDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/subservice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
