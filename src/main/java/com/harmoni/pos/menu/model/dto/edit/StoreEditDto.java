package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.StoreDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StoreEditDto extends StoreDto {
    @NotNull(message = "{validation.store.id.NotBlank}")
    private Integer id;
}
