package com.harmoni.pos.menu.model.dto;


import com.harmoni.pos.menu.model.User;
import io.github.weasleyj.mybatis.encrypt.annotation.Encryption;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank(message = "{validation.user.username.NotBlank}")
    @Size(min = 2, max = 125)
    @Email(message = "{validation.user.email.NotValid}",
            regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
    flags = Pattern.Flag.CASE_INSENSITIVE) //RFC 5322 for Email Validation
    private String username;
    @NotBlank(message = "{validation.user.password.NotBlank}")
    @Size(min = 2, max = 255)
    private String password;

    public User toUser() {
        return new User()
                .setEmail(username)
                .setPassword(password);
    }

}
