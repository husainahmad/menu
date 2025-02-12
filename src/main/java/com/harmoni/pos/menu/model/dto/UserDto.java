package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    @NotNull(message = "{validation.user.authId.NotNull}")
    private Integer authId;
    @NotBlank(message = "{validation.user.name.NotNull}")
    private String username;
    @NotNull(message = "{validation.user.storeId.NotNull}")
    private Integer storeId;
    @NotBlank(message = "{validation.user.password.NotNull}")
    private String password;
    private String email;

    public User toUser() {
        return new User().setStoreId(storeId)
                .setUsername(username)
                .setAuthId(authId);
    }
}
