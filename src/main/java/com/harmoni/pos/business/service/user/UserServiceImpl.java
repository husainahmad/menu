package com.harmoni.pos.business.service.user;

import com.harmoni.pos.component.JWTTokenUtil;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.UserMapper;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.UserLoginDto;
import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties;
import io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MybatisEncryptProperties mybatisEncryptProperties;

    @Override
    public String authenticate(UserLoginDto userLoginDto) {

        User user = EncryptStrategy.convert(userLoginDto.toUser(),
                mybatisEncryptProperties.getEncryptType());

        if (ObjectUtils.isEmpty(userMapper.selectByEmailPassword(user.getEmail(),
                user.getPassword()))) {
            throw new BusinessBadRequestException("exception.user.login.badRequest.incorrect", null);
        }

        return JWTTokenUtil.generateToken(userLoginDto.getUsername());
    }

    @Override
    public int create(UserDto userDto) {

        if (!ObjectUtils.isEmpty(userMapper.selectByEmail(userDto.getEmail()))) {
            throw new BusinessBadRequestException("exception.user.email.badRequest.duplicate", null);
        }

        int inserted = userMapper.insert(userDto.toUser());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }
}
