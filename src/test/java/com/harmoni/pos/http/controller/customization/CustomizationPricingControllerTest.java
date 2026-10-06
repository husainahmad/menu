package com.harmoni.pos.http.controller.customization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.business.service.customization.CustomizationPricingService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.handler.BadRequestExceptionHandler;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceResponseDto;
import com.harmoni.pos.menu.model.dto.pricing.PricedCustomizationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomizationPricingControllerTest {

    @Mock
    private CustomizationPricingService customizationPricingService;

    @InjectMocks
    private CustomizationPricingController customizationPricingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        mockMvc = MockMvcBuilders.standaloneSetup(customizationPricingController)
                .setControllerAdvice(new BadRequestExceptionHandler(messageSource))
                .build();
        objectMapper = new ObjectMapper();
    }

    private static CustomizationPriceRequestDto request() {
        CustomizationPriceRequestDto request = new CustomizationPriceRequestDto();
        request.setProductId(10);
        request.setSkuId(20);
        request.setQuantity(2);
        request.setCustomizationOptionIds(List.of(101));
        return request;
    }

    private static CustomizationPriceResponseDto response() {
        PricedCustomizationDto priced = new PricedCustomizationDto();
        priced.setCustomizationOptionId(101);
        priced.setCustomizationId(1);
        priced.setCustomizationName("Size");
        priced.setOptionName("Large");
        priced.setPrice(new BigDecimal("1.50"));
        priced.setAmount(new BigDecimal("3.00"));

        CustomizationPriceResponseDto response = new CustomizationPriceResponseDto();
        response.setProductId(10);
        response.setSkuId(20);
        response.setCustomizations(List.of(priced));
        response.setTotalAmount(new BigDecimal("3.00"));
        return response;
    }

    @Test
    void price_shouldReturn200WithThePricedChoices() throws Exception {
        when(customizationPricingService.price(any(CustomizationPriceRequestDto.class), eq("cashier")))
                .thenReturn(response());

        mockMvc.perform(post("/api/v1/customization/price")
                        .header("X-Username", "cashier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(3.00))
                .andExpect(jsonPath("$.data.customizations[0].optionName").value("Large"))
                .andExpect(jsonPath("$.data.customizations[0].price").value(1.50))
                .andExpect(jsonPath("$.data.customizations[0].amount").value(3.00));

        verify(customizationPricingService).price(any(CustomizationPriceRequestDto.class), eq("cashier"));
    }

    @Test
    void price_shouldPassTheOperatorsUsernameForTierResolution() throws Exception {
        when(customizationPricingService.price(any(CustomizationPriceRequestDto.class), eq("manager")))
                .thenReturn(response());

        mockMvc.perform(post("/api/v1/customization/price")
                        .header("X-Username", "manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk());

        verify(customizationPricingService).price(any(CustomizationPriceRequestDto.class), eq("manager"));
    }

    @Test
    void price_shouldReturn400WhenTheChoicesBreakACustomizationRule() throws Exception {
        when(customizationPricingService.price(any(CustomizationPriceRequestDto.class), eq("cashier")))
                .thenThrow(new BusinessBadRequestException(
                        "exception.customizationPrice.required", new Object[]{"Size"}));

        mockMvc.perform(post("/api/v1/customization/price")
                        .header("X-Username", "cashier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        org.hamcrest.Matchers.containsString("Size")));
    }
}