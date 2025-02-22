package com.harmoni.pos.business.service.user;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.auth.AuthService;
import com.harmoni.pos.business.service.brand.BrandService;
import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.component.JwtUtil;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.mapper.UserMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StoreService storeService;
    private final ChainService chainService;
    private final BrandService brandService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private static final String AUTH_FAILED_EXCEPTION = "exception.auth.process.Failed";
    private static final String USER_NOT_FOUND_EXCEPTION = "exception.user.id.NotFound";
    private static final String USER_AUTH_ID_EXCEPTION = "exception.user.authId.NotFound";

    @Override
    public int insert(String token, UserDto userDto) {
        if (authService.create(token, userDto)==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        if ((ObjectUtils.isNotEmpty(this.selectByUsernameAuthIdAndStoreId(userDto.getUsername(), userDto.getAuthId(),
                userDto.getStoreId())))) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }

        return userMapper.insert(userDto.toUser());
    }

    @Override
    public int update(String token, UserEditDto userEditDto) {
        User user = selectById(userEditDto.getId());
        user.setUsername(userEditDto.getUsername());
        user.setStoreId(userEditDto.getStoreId());
        user.setAuthId(userEditDto.getAuthId());
        if (authService.update(token, userEditDto)==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    public User selectById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(String token, Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }
        if (authService.delete(token, user.getUsername())==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        return userMapper.deleteByPrimaryKey(user.getId());
    }

    @Override
    public User selectByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_AUTH_ID_EXCEPTION, null);
        }
        Store store = storeService.get(user.getId());
        user.setStore(store);
        Chain chain = chainService.get(store.getChainId());
        store.setChain(chain);
        Brand brand = brandService.get(chain.getBrandId());
        chain.setBrand(brand);
        return user;
    }

    @Override
    public User selectByAuthToken(String authToken) {
        return this.selectByUsername(jwtUtil.extractUsername(authToken));
    }

    @Override
    public User selectByUsernameAuthIdAndStoreId(String name, Integer authId, Integer storeId) {
        return userMapper.selectByUsernameAuthIdAndStoreId(name, authId, storeId);
    }

    @Override
    public List<User> selectByStoreIds(List<Store> stores, String search) {
        return userMapper.selectByListChain(stores, search);
    }

    @Override
    public Map<String, Object> selectByChainId(Integer chainId, int page, int size, String search) {
        PaginationUtils.applyPagination(page, size);

        List<Store> stores = this.storeService.getAllStoresByChainId(chainId, "");
        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<User> productPageInfo = new PageInfo<>(selectByStoreIds(stores, search));

        paginationData.put("page", productPageInfo.getPages());
        paginationData.put("size", productPageInfo.getSize());
        paginationData.put("total", productPageInfo.getTotal());
        paginationData.put("data", productPageInfo.getList());
        paginationData.put("navigate", productPageInfo.getNavigatepageNums());

        return paginationData;
    }

}
