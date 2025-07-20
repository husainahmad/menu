package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for editing a User.
 * Inherits properties from {@link UserDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserEditDto extends UserDto {
    /**
     * The ID of the user to edit.
     */
    @NotNull(message = "{validation.user.id.NotNull}")
    private Integer id;
}
