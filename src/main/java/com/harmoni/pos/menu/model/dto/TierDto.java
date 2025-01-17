package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TierDto {

    @NotBlank(message = "{validation.tier.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;
    @NotNull(message = "{validation.tier.brandId.NotNull}")
    private Integer brandId;
    @NotNull(message = "{validation.tier.type.NotNull}")
    private TierType type;

    public Tier toTear() {
        return new Tier()
                .setName(name)
                .setBrandId(brandId)
                .setType(type);
    }

}