package com.harmoni.pos.http.controller.product;

import com.harmoni.pos.business.service.product.ProductService;
import com.harmoni.pos.business.service.product.ProductSkuService;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
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
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductSkuService productSkuService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        ProductAddDto dto = new ProductAddDto();
        dto.setName("Burger");
        dto.setCategoryId(1);
        when(productSkuService.create(any(ProductAddDto.class))).thenReturn(new Product().setId(1));

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(productService.get(1)).thenReturn(new Product().setId(1).setName("Burger"));

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Burger"));
    }

    @Test
    void getByIds_shouldReturn200() throws Exception {
        when(productService.getByList(List.of(1, 2), "ahmad.husain")).thenReturn(List.of(new Product().setId(1)));

        mockMvc.perform(get("/api/v1/product")
                        .header("X-Username", "ahmad.husain")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void put_shouldReturn200() throws Exception {
        ProductEditDto editDto = new ProductEditDto();
        editDto.setName("Burger");
        editDto.setCategoryId(1);
        editDto.setId(1);
        when(productSkuService.update(any(ProductEditDto.class))).thenReturn(new Product().setId(1));

        mockMvc.perform(put("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(productService.delete(anyInt())).thenReturn(1);

        mockMvc.perform(delete("/api/v1/product/1"))
                .andExpect(status().isOk());
    }


    private static com.harmoni.pos.menu.model.CustomizationOption option(String name,
                                                                         java.math.BigDecimal price) {
        var tierPrice = new com.harmoni.pos.menu.model.CustomizationOptionTierPrice();
        tierPrice.setTierId(20);
        tierPrice.setPrice(price);
        var option = new com.harmoni.pos.menu.model.CustomizationOption();
        option.setId(1001);
        option.setName(name);
        option.setTierPrices(List.of(tierPrice));
        return option;
    }

    private static Product productWithCustomization() {
        var customization = new com.harmoni.pos.menu.model.Customization();
        customization.setId(100);
        customization.setName("Toppings");
        customization.setRequired(true);
        customization.setMinimumSelection(2);
        customization.setCustomizationOptions(List.of(option("Cheese", new java.math.BigDecimal("1.50"))));
        return new Product().setId(1).setName("Burger").setCustomizations(List.of(customization));
    }

    @Test
    void getByCategory_shouldReturnCustomizationsAndOptions() throws Exception {
        when(productService.selectByCategory(1)).thenReturn(List.of(productWithCustomization()));

        mockMvc.perform(get("/api/v1/product/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customizations[0].name").value("Toppings"))
                .andExpect(jsonPath("$.data[0].customizations[0].required").value(true))
                .andExpect(jsonPath("$.data[0].customizations[0].minimumSelection").value(2))
                .andExpect(jsonPath("$.data[0].customizations[0].customizationOptions[0].name").value("Cheese"))
                .andExpect(jsonPath("$.data[0].customizations[0].customizationOptions[0].tierPrices[0].price")
                        .value(1.50));
    }

    @Test
    void getByCategoryPrice_shouldReturnCustomizationsAndOptions() throws Exception {
        when(productService.selectByCategoryPrice("ahmad.husain", 1))
                .thenReturn(List.of(productWithCustomization()));

        mockMvc.perform(get("/api/v1/product/category/1/price")
                        .header("X-Username", "ahmad.husain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customizations[0].name").value("Toppings"))
                .andExpect(jsonPath("$.data[0].customizations[0].customizationOptions[0].name").value("Cheese"))
                .andExpect(jsonPath("$.data[0].customizations[0].customizationOptions[0].tierPrices[0].price")
                        .value(1.50));
    }

    @Test
    void getByCategoryBrand_shouldReturnCustomizationsAndOptions() throws Exception {
        var pagination = new java.util.HashMap<String, Object>();
        pagination.put("data", List.of(productWithCustomization()));
        when(productService.selectByCategoryBrand(1, 2, 1, 10, "search")).thenReturn(pagination);

        mockMvc.perform(get("/api/v1/product/category/1/2")
                        .param("page", "1").param("size", "10").param("search", "search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].customizations[0].customizationOptions[0].name")
                        .value("Cheese"));
    }

    @Test
    void getByCategory_shouldReturn200() throws Exception {
        when(productService.selectByCategory(1)).thenReturn(List.of(new Product().setId(1)));

        mockMvc.perform(get("/api/v1/product/category/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getByCategoryPrice_shouldReturn200() throws Exception {
        when(productService.selectByCategoryPrice("ahmad.husain", 1)).thenReturn(List.of(new Product().setId(1)));

        mockMvc.perform(get("/api/v1/product/category/1/price")
                        .header("X-Username", "ahmad.husain"))
                .andExpect(status().isOk());
    }

    @Test
    void getByCategoryBrand_shouldReturn200() throws Exception {
        when(productService.selectByCategoryBrand(1, 2, 1, 10, "search"))
                .thenReturn(Map.of("data", "result"));

        mockMvc.perform(get("/api/v1/product/category/1/2")
                        .param("page", "1")
                        .param("size", "10")
                        .param("search", "search"))
                .andExpect(status().isOk());
    }
}
