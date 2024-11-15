package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Chain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class ChainDto {
    @NotBlank(message = "{validation.chain.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    @NotNull(message = "{validation.chain.brandId.NotNull}")
    private Integer brandId;

    public Chain toChain() {
        return new Chain()
                .setName(name)
                .setBrandId(brandId)
                .setCreatedAt(new Date(System.currentTimeMillis()));
    }
}
