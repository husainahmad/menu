package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Chain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * Data transfer object for Chain.
 * Used for transferring chain data between layers.
 */
@Data
public class ChainDto {
    /**
     * The name of the chain.
     */
    @NotBlank(message = "{validation.chain.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The brand ID associated with the chain.
     */
    @NotNull(message = "{validation.chain.brandId.NotNull}")
    private Integer brandId;

    /**
     * Converts this DTO to a Chain entity.
     *
     * @return a Chain entity with name, brandId, and createdAt set
     */
    public Chain toChain() {
        return new Chain()
                .setName(name)
                .setBrandId(brandId)
                .setCreatedAt(new Date(System.currentTimeMillis()));
    }
}
