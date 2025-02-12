package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserEditDto extends UserDto {
    @NotNull(message = "{validation.user.id.NotNull}")
    private Integer id;
}
