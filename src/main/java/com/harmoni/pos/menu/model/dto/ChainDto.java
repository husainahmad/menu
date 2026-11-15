package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Chain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
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
     * The chain type: {@code REGION}, {@code FRANCHISE} or {@code OTHER}.
     */
    private String type;

    /**
     * The email address of the chain manager.
     */
    private String managerEmail;

    /**
     * Whether tax/service settings are inherited from the brand
     * ({@code INHERIT}) or set on the chain ({@code OWN}).
     */
    private String taxServiceMode;

    /**
     * The tax percentage when {@link #taxServiceMode} is {@code OWN}.
     */
    private BigDecimal taxPercent;

    /**
     * The service-charge percentage when {@link #taxServiceMode} is {@code OWN}.
     */
    private BigDecimal serviceChargePercent;

    /**
     * Converts this DTO to a Chain entity.
     *
     * @return a Chain entity with the fields set
     */
    public Chain toChain() {
        return new Chain()
                .setName(name)
                .setBrandId(brandId)
                .setType(type == null ? "REGION" : type)
                .setManagerEmail(managerEmail)
                .setTaxServiceMode(taxServiceMode == null ? "INHERIT" : taxServiceMode)
                .setTaxPercent(taxPercent)
                .setServiceChargePercent(serviceChargePercent)
                .setCreatedAt(new Date(System.currentTimeMillis()));
    }
}
