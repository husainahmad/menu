package com.harmoni.pos.http.controller.category;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;
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
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Drinks");
        dto.setDescription("Beverages");
        dto.setBrandId(1);
        when(categoryService.create(any(CategoryDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(categoryService.listPaginated("ahmad.husain", 1, 10)).thenReturn(Map.of("data", List.of()));

        mockMvc.perform(get("/api/v1/category")
                        .header("X-Username", "ahmad.husain")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(categoryService.get(1)).thenReturn(new Category().setId(1).setName("Drinks"));

        mockMvc.perform(get("/api/v1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Drinks"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(categoryService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/category/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getByBrandId_shouldReturn200() throws Exception {
        when(categoryService.selectByBrandId(1)).thenReturn(List.of(new Category().setId(1)));

        mockMvc.perform(get("/api/v1/category/brand/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(categoryService.getListByUserAuth("ahmad.husain")).thenReturn(List.of(new Category().setId(1)));

        mockMvc.perform(get("/api/v1/category/tier")
                        .header("X-Username", "ahmad.husain"))
                .andExpect(status().isOk());
    }
}
