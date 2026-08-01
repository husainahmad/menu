package com.harmoni.pos.business.service.user;

import com.harmoni.pos.business.service.auth.AuthService;
import com.harmoni.pos.business.service.brand.BrandService;
import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.component.JwtUtil;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.UserMapper;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StoreService storeService;

    @Mock
    private ChainService chainService;

    @Mock
    private BrandService brandService;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private UserEditDto userEditDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setAuthId(1);
        userDto.setUsername("john");
        userDto.setStoreId(10);
        userDto.setPassword("pass123");

        userEditDto = new UserEditDto();
        userEditDto.setId(1);
        userEditDto.setUsername("john");
        userEditDto.setStoreId(10);
        userEditDto.setAuthId(1);

        Brand brand = new Brand().setId(1).setName("Brand1");
        Chain chain = new Chain().setId(1).setBrandId(1).setBrand(brand);
        Store store = new Store().setId(10).setChainId(1).setChain(chain);

        user = new User().setId(1).setUsername("john").setStoreId(10)
                .setAuthId(1).setStore(store);
    }

    @Test
    void insert_shouldSucceed() {
        when(authService.create(anyString(), eq(userDto))).thenReturn(200);
        when(userMapper.selectByUsernameAuthIdAndStoreId("john", 1, 10)).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        int result = userService.insert("Bearer token", userDto);
        assertEquals(1, result);
    }

    @Test
    void insert_shouldThrow_whenAuthFails() {
        when(authService.create(anyString(), eq(userDto))).thenReturn(0);
        assertThrows(BusinessNotFoundRequestException.class, () -> userService.insert("Bearer token", userDto));
    }

    @Test
    void insert_shouldThrow_whenUserExists() {
        when(authService.create(anyString(), eq(userDto))).thenReturn(200);
        when(userMapper.selectByUsernameAuthIdAndStoreId("john", 1, 10)).thenReturn(user);

        assertThrows(BusinessNotFoundRequestException.class, () -> userService.insert("Bearer token", userDto));
    }

    @Test
    void update_shouldSucceed() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(user);
        when(authService.update(anyString(), eq(userEditDto))).thenReturn(200);
        when(userMapper.updateByPrimaryKey(any(User.class))).thenReturn(1);

        int result = userService.update("Bearer token", userEditDto);
        assertEquals(1, result);
    }

    @Test
    void update_shouldThrow_whenAuthFails() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(user);
        when(authService.update(anyString(), eq(userEditDto))).thenReturn(0);

        assertThrows(BusinessNotFoundRequestException.class, () -> userService.update("Bearer token", userEditDto));
    }

    @Test
    void selectById_shouldReturnUser() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(user);
        assertNotNull(userService.selectById(1));
    }

    @Test
    void selectById_shouldThrow_whenNotFound() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> userService.selectById(1));
    }

    @Test
    void delete_shouldSucceed() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(user);
        when(authService.delete(anyString(), eq("john"))).thenReturn(200);
        when(userMapper.deleteByPrimaryKey(1)).thenReturn(1);

        int result = userService.delete("Bearer token", 1);
        assertEquals(1, result);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> userService.delete("Bearer token", 1));
    }

    @Test
    void delete_shouldThrow_whenAuthFails() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(user);
        when(authService.delete(anyString(), eq("john"))).thenReturn(0);

        assertThrows(BusinessNotFoundRequestException.class, () -> userService.delete("Bearer token", 1));
    }

    @Test
    void selectByUsername_shouldReturnUser() {
        when(userMapper.selectByUsername("john")).thenReturn(user);
        when(storeService.get(10)).thenReturn(user.getStore());
        when(chainService.get(1)).thenReturn(user.getStore().getChain());
        when(brandService.get(1)).thenReturn(brand);

        User result = userService.selectByUsername("john");
        assertNotNull(result);
        assertNotNull(result.getStore().getChain().getBrand());
    }

    @Test
    void selectByUsername_shouldThrow_whenNotFound() {
        when(userMapper.selectByUsername("john")).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> userService.selectByUsername("john"));
    }

    @Test
    void selectByAuthToken_shouldResolveUser() {
        when(jwtUtil.extractUsername("token123")).thenReturn("john");
        when(userMapper.selectByUsername("john")).thenReturn(user);
        when(storeService.get(10)).thenReturn(user.getStore());
        when(chainService.get(1)).thenReturn(user.getStore().getChain());
        when(brandService.get(1)).thenReturn(brand);

        User result = userService.selectByAuthToken("token123");
        assertNotNull(result);
    }

    @Test
    void selectByUsernameAuthIdAndStoreId_shouldReturn() {
        when(userMapper.selectByUsernameAuthIdAndStoreId("john", 1, 10)).thenReturn(user);
        assertNotNull(userService.selectByUsernameAuthIdAndStoreId("john", 1, 10));
    }

    @Test
    void selectByStoreIds_shouldReturnList() {
        Store store = new Store().setId(10);
        when(userMapper.selectByListChain(List.of(store), "")).thenReturn(List.of(user));
        assertFalse(userService.selectByStoreIds(List.of(store), "").isEmpty());
    }

    private final Brand brand = new Brand().setId(1).setName("Brand1");
}
