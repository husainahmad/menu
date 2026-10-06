package com.harmoni.pos.http.controller.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.business.service.validation.OrderItemValidationService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.handler.BadRequestExceptionHandler;
import com.harmoni.pos.http.handler.ValidationExceptionHandler;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsRequest;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsResponseDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedCustomizationDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedOrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InternalMenuControllerTest {

    @Mock
    private OrderItemValidationService orderItemValidationService;

    @InjectMocks
    private InternalMenuController internalMenuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        mockMvc = MockMvcBuilders.standaloneSetup(internalMenuController)
                .setControllerAdvice(new BadRequestExceptionHandler(messageSource),
                        new ValidationExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    private static Map<String, Object> requestBody() {
        return Map.of("storeId", 1,
                "items", List.of(Map.of(
                        "productId", 10,
                        "skuId", 101,
                        "quantity", 2,
                        "customizations", List.of(
                                Map.of("customizationId", 100, "options", List.of(
                                        Map.of("customizationOptionId", 1001, "quantity", 1))),
                                Map.of("customizationId", 200, "options", List.of(
                                        Map.of("customizationOptionId", 1005, "quantity", 1)))))));
    }

    private static ValidateOrderItemsResponseDto response() {
        ValidatedCustomizationDto ice = new ValidatedCustomizationDto();
        ice.setCustomizationOptionId(1001);
        ice.setCustomizationId(100);
        ice.setCustomizationName("Ice");
        ice.setOptionName("Less Ice");
        ice.setPrice(BigDecimal.ZERO);
        ice.setQuantity(1);

        ValidatedCustomizationDto oat = new ValidatedCustomizationDto();
        oat.setCustomizationOptionId(1005);
        oat.setCustomizationId(200);
        oat.setCustomizationName("Milk");
        oat.setOptionName("Oat Milk");
        oat.setPrice(new BigDecimal("4000"));
        oat.setQuantity(1);

        ValidatedOrderItemDto item = new ValidatedOrderItemDto();
        item.setProductId(10);
        item.setProductName("Kopi Gula Aren");
        item.setSkuId(101);
        item.setSkuName("Large");
        item.setSkuPrice(new BigDecimal("20000"));
        item.setQuantity(2);
        item.setCustomizations(List.of(ice, oat));

        ValidateOrderItemsResponseDto response = new ValidateOrderItemsResponseDto();
        response.setValid(true);
        response.setItems(List.of(item));
        return response;
    }

    @Test
    void validateOrderItems_shouldReturn200WithTheOfficialMenuData() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class))).thenReturn(response());

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.items[0].productId").value(10))
                .andExpect(jsonPath("$.data.items[0].productName").value("Kopi Gula Aren"))
                .andExpect(jsonPath("$.data.items[0].skuId").value(101))
                .andExpect(jsonPath("$.data.items[0].skuName").value("Large"))
                .andExpect(jsonPath("$.data.items[0].skuPrice").value(20000))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].customizations[0].customizationOptionId").value(1001))
                .andExpect(jsonPath("$.data.items[0].customizations[0].customizationName").value("Ice"))
                .andExpect(jsonPath("$.data.items[0].customizations[0].optionName").value("Less Ice"))
                .andExpect(jsonPath("$.data.items[0].customizations[0].price").value(0))
                .andExpect(jsonPath("$.data.items[0].customizations[0].quantity").value(1))
                .andExpect(jsonPath("$.data.items[0].customizations[1].customizationOptionId").value(1005))
                .andExpect(jsonPath("$.data.items[0].customizations[1].customizationName").value("Milk"))
                .andExpect(jsonPath("$.data.items[0].customizations[1].optionName").value("Oat Milk"))
                .andExpect(jsonPath("$.data.items[0].customizations[1].price").value(4000))
                .andExpect(jsonPath("$.data.items[0].customizations[1].quantity").value(1));

        verify(orderItemValidationService).validate(any(ValidateOrderItemsRequest.class));
    }

    @Test
    void validateOrderItems_shouldNotReturnAnyTotalForTheLine() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class))).thenReturn(response());

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].subtotal").doesNotExist())
                .andExpect(jsonPath("$.data.items[0].totalAmount").doesNotExist())
                .andExpect(jsonPath("$.data.items[0].amount").doesNotExist())
                .andExpect(jsonPath("$.data.items[0].customizations[0].amount").doesNotExist())
                .andExpect(jsonPath("$.data.totalAmount").doesNotExist());
    }

    @Test
    void validateOrderItems_shouldReturn400WhenTheProductCannotBeSold() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class)))
                .thenThrow(new BusinessBadRequestException(
                        "exception.orderValidation.productNotAvailable", new Object[]{10, 1}));

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.error", containsString("product 10 is not available at store 1")));
    }

    @Test
    void validateOrderItems_shouldReturn400WhenTheSkuBelongsToAnotherProduct() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class)))
                .thenThrow(new BusinessBadRequestException(
                        "exception.orderValidation.skuProductMismatch", new Object[]{101, 10}));

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("sku 101 does not belong to product 10")));
    }

    @Test
    void validateOrderItems_shouldReturn400WhenARequiredCustomizationIsMissing() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class)))
                .thenThrow(new BusinessBadRequestException(
                        "exception.customizationPrice.required", new Object[]{"Ice"}));

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Ice is required")));
    }

    @Test
    void validateOrderItems_shouldReturn400WhenAQuantityIsNotValid() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class)))
                .thenThrow(new BusinessBadRequestException(
                        "exception.orderValidation.invalidQuantity", new Object[]{0, 100}));

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("quantity 0 is not valid")));
    }

    @Test
    void validateOrderItems_shouldRejectABasketWithNoLines() throws Exception {
        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("storeId", 1, "items", List.of()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", hasItem(containsString("items"))));

        verifyNoInteractions(orderItemValidationService);
    }

    @Test
    void validateOrderItems_shouldRejectABasketWithNoStore() throws Exception {
        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("items",
                                List.of(Map.of("productId", 10, "skuId", 101, "quantity", 1))))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", hasItem(containsString("storeId"))));

        verifyNoInteractions(orderItemValidationService);
    }

    @Test
    void validateOrderItems_shouldRejectALineWithNoProductOrSku() throws Exception {
        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("storeId", 1,
                                "items", List.of(Map.of("quantity", 1))))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", hasItem(containsString("productId"))))
                .andExpect(jsonPath("$.error", hasItem(containsString("skuId"))));

        verifyNoInteractions(orderItemValidationService);
    }

    @Test
    void validateOrderItems_shouldRejectAChoiceWithNoOptionId() throws Exception {
        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("storeId", 1,
                                "items", List.of(Map.of("productId", 10, "skuId", 101, "quantity", 1,
                                        "customizations", List.of(Map.of("customizationId", 100,
                                                "options", List.of(Map.of("quantity", 1))))))))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", hasItem(containsString("customizationOptionId"))));

        verifyNoInteractions(orderItemValidationService);
    }

    @Test
    void validateOrderItems_shouldAcceptALineThatNamedNoCustomizations() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class))).thenReturn(response());

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("storeId", 1,
                                "items", List.of(Map.of("productId", 10, "skuId", 101, "quantity", 1))))))
                .andExpect(status().isOk());

        verify(orderItemValidationService).validate(any(ValidateOrderItemsRequest.class));
    }

    @Test
    void validateOrderItems_shouldPassTheStoreAndEveryLineToTheService() throws Exception {
        when(orderItemValidationService.validate(any(ValidateOrderItemsRequest.class))).thenReturn(response());

        mockMvc.perform(post("/internal/v1/menu/validate-order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isOk());

        org.mockito.ArgumentCaptor<ValidateOrderItemsRequest> captor =
                org.mockito.ArgumentCaptor.forClass(ValidateOrderItemsRequest.class);
        verify(orderItemValidationService).validate(captor.capture());
        ValidateOrderItemsRequest sent = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(1, sent.getStoreId());
        org.junit.jupiter.api.Assertions.assertEquals(1, sent.getItems().size());
        org.junit.jupiter.api.Assertions.assertEquals(10, sent.getItems().get(0).getProductId());
        org.junit.jupiter.api.Assertions.assertEquals(101, sent.getItems().get(0).getSkuId());
        org.junit.jupiter.api.Assertions.assertEquals(2, sent.getItems().get(0).getQuantity());
        org.junit.jupiter.api.Assertions.assertEquals(2, sent.getItems().get(0).getCustomizations().size());
        org.junit.jupiter.api.Assertions.assertEquals(100,
                sent.getItems().get(0).getCustomizations().get(0).getCustomizationId());
        org.junit.jupiter.api.Assertions.assertEquals(1001,
                sent.getItems().get(0).getCustomizations().get(0).getOptions().get(0).getCustomizationOptionId());
    }
}