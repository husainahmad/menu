package com.harmoni.pos.http.controller.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.business.service.device.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getConfigurationReturnsJson() throws Exception {
        JsonNode configuration = objectMapper.readTree(
                "{\"deviceMode\":\"CASHIER_POS\",\"payments\":[\"CASH\"]}");
        when(deviceService.getConfigurationByAddress("POS-02")).thenReturn(configuration);

        mockMvc.perform(get("/api/v1/devices/configuration").param("address", "POS-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.deviceMode").value("CASHIER_POS"))
                .andExpect(jsonPath("$.data.payments[0]").value("CASH"));

        verify(deviceService).getConfigurationByAddress("POS-02");
    }
}
