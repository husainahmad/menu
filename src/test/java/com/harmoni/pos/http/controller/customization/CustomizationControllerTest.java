package com.harmoni.pos.http.controller.customization;

import com.harmoni.pos.business.service.customization.CustomizationService;
import com.harmoni.pos.menu.model.Customization;
import com.harmoni.pos.menu.model.SelectionType;
import com.harmoni.pos.menu.model.dto.CustomizationDto;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomizationControllerTest {

    @Mock
    private CustomizationService customizationService;

    @InjectMocks
    private CustomizationController customizationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customizationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CustomizationDto dto = new CustomizationDto();
        dto.setName("Extra Cheese");
        dto.setSelectionType(SelectionType.SINGLE);
        dto.setCustomizationOptions(new ArrayList<>());
        when(customizationService.createCustomization(anyString(), any(Customization.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/customization")
                        .header("X-Username", "ahmad.husain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(customizationService.listPaginated("ahmad.husain", 1, 10)).thenReturn(Map.of("data", "test"));

        mockMvc.perform(get("/api/v1/customization")
                        .header("X-Username", "ahmad.husain")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(customizationService.getCustomizationById(1)).thenReturn(Optional.of(new Customization().setId(1)));

        mockMvc.perform(get("/api/v1/customization/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(customizationService.deleteCustomization(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/customization/1"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        CustomizationDto dto = new CustomizationDto();
        dto.setName("Extra Cheese");
        dto.setSelectionType(SelectionType.SINGLE);
        dto.setCustomizationOptions(new ArrayList<>());
        when(customizationService.updateCustomization(any(Customization.class))).thenReturn(1);

        mockMvc.perform(put("/api/v1/customization/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getByProductId_shouldReturn200() throws Exception {
        when(customizationService.getCustomizationById(1)).thenReturn(Optional.of(new Customization().setId(1)));

        mockMvc.perform(get("/api/v1/customization/product/1"))
                .andExpect(status().isOk());
    }
}
