package com.harmoni.pos.business.service.user;

import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.UserLoginDto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface UserService {

    String authenticate(UserLoginDto userLoginDto);
    int create(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
}
