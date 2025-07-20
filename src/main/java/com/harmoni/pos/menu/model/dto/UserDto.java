package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for User.
 * Used for transferring user data between layers.
 */
@Data
public class UserDto {
    /**
     * The authentication ID for the user.
     */
    @NotNull(message = "{validation.user.authId.NotNull}")
    private Integer authId;

    /**
     * The username of the user.
     */
    @NotBlank(message = "{validation.user.name.NotNull}")
    private String username;

    /**
     * The store ID associated with the user.
     */
    @NotNull(message = "{validation.user.storeId.NotNull}")
    private Integer storeId;

    /**
     * The password for the user.
     */
    @NotBlank(message = "{validation.user.password.NotNull}")
    private String password;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * Converts this DTO to a User entity.
     *
     * @return a User entity with storeId, username, and authId set
     */
    public User toUser() {
        return new User().setStoreId(storeId)
                .setUsername(username)
                .setAuthId(authId);
    }
}
