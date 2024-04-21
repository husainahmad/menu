package com.harmoni.pos.menu.model.dto;


import com.harmoni.pos.menu.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "{validation.user.email.NotBlank}")
    @Size(min = 2, max = 125)
    @Email(message = "{validation.user.email.NotValid}",
            regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
    flags = Pattern.Flag.CASE_INSENSITIVE) //RFC 5322 for Email Validation
    private String email;
    @NotBlank(message = "{validation.user.name.NotBlank}")
    @Size(min = 2, max = 125)
    private String name;
    @NotBlank(message = "{validation.user.password.NotBlank}")
    @Size(min = 2, max = 255)
    private String password;

    @NotNull(message = "{validation.user.storeId.NotNull}")
    private Integer storeId;
    @NotNull(message = "{validation.user.roleId.NotNull}")
    private Integer roleId;

    public User toUser() {
        return new User()
                .setEmail(email)
                .setName(name)
                .setPassword(password)
                .setRoleId(roleId)
                .setStoreId(storeId);
    }
}
