package com.harmoni.pos.http.controller.user;

import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
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

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setStoreId(2);
        dto.setAuthId(1);
        dto.setPassword("pass123");
        when(userService.insert(anyString(), any(UserDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/user")
                        .header("X-Username", "ahmad.husain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getByCategoryBrand_shouldReturn200() throws Exception {
        when(userService.selectByChainId(1, 1, 10, "search"))
                .thenReturn(Map.of("data", "result"));

        mockMvc.perform(get("/api/v1/user/chain/1")
                        .param("page", "1")
                        .param("size", "10")
                        .param("search", "search"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        when(userService.delete("ahmad.husain", 1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/user/1")
                        .header("X-Username", "ahmad.husain"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UserEditDto editDto = new UserEditDto();
        editDto.setId(1);
        editDto.setUsername("john");
        editDto.setStoreId(2);
        editDto.setAuthId(1);
        editDto.setPassword("pass123");
        when(userService.update("ahmad.husain", editDto)).thenReturn(1);

        mockMvc.perform(put("/api/v1/user/1")
                        .header("X-Username", "ahmad.husain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk());
    }

    @Test
    void detailUser_shouldReturn200() throws Exception {
        when(userService.selectByUsername("john")).thenReturn(new User().setUsername("john"));

        mockMvc.perform(get("/api/v1/user/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("john"));
    }
}
