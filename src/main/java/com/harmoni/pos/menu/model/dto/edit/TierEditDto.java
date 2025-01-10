package com.harmoni.pos.menu.model.dto.edit;

import com.harmoni.pos.menu.model.dto.TierDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TierEditDto extends TierDto {
    @NotNull(message = "{validation.tier.id.NotNull}")
    private Integer id;
}