package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Store.
 * Used for transferring store data between layers.
 */
@Data
public class StoreDto {

    /**
     * The name of the store.
     */
    @NotBlank(message = "{validation.store.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The chain ID associated with the store.
     */
    @NotNull(message = "{validation.store.chainId.NotNull}")
    private Integer chainId;

    /**
     * The address of the store.
     */
    @NotBlank(message = "{validation.store.address.NotBlank}")
    private String address;

    /**
     * The order types enabled at the store, comma separated.
     */
    private String orderTypes;

    /**
     * Whether tax/service settings are inherited from the chain
     * ({@code INHERIT}) or set on the store ({@code OWN}).
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
     * Whether the receipt logo is inherited from the brand ({@code INHERIT})
     * or set on the store ({@code OWN}).
     */
    private String receiptLogoMode;

    /**
     * The store-specific receipt logo URL when {@link #receiptLogoMode} is
     * {@code OWN}.
     */
    private String logoUrl;

    /**
     * Whether menu/price settings are inherited from the brand
     * ({@code INHERIT}) or set on the store ({@code OWN}).
     */
    private String menuPriceMode;

    /**
     * Whether order types are inherited from the chain ({@code INHERIT}) or
     * set on the store ({@code OWN}).
     */
    private String orderTypeMode;

    /**
     * The tier menu ID for the store.
     */
    private Integer tierMenuId;

    /**
     * The tier price ID for the store.
     */
    private Integer tierPriceId;

    /**
     * The tier service ID for the store.
     */
    private Integer tierServiceId;

    /**
     * Converts this DTO to a Store entity.
     *
     * @return a Store entity with fields set from this DTO
     */
    public Store toStore() {
        return new Store()
                .setName(name)
                .setChainId(chainId)
                .setAddress(address)
                .setOrderTypes(orderTypes == null ? "DINE_IN" : orderTypes)
                .setTaxServiceMode(taxServiceMode == null ? "INHERIT" : taxServiceMode)
                .setTaxPercent(taxPercent)
                .setServiceChargePercent(serviceChargePercent)
                .setReceiptLogoMode(receiptLogoMode == null ? "INHERIT" : receiptLogoMode)
                .setLogoUrl(logoUrl)
                .setMenuPriceMode(menuPriceMode == null ? "INHERIT" : menuPriceMode)
                .setOrderTypeMode(orderTypeMode == null ? "INHERIT" : orderTypeMode)
                .setTierPriceId(tierPriceId)
                .setTierServiceId(tierServiceId)
                .setTierMenuId(tierMenuId);
    }

    /**
     * Converts this DTO to a StoreTier entity.
     *
     * @return a StoreTier entity with tier IDs set from this DTO
     */
    public StoreTier toStoreTier() {
        return new StoreTier()
                .setTierPriceId(tierPriceId)
                .setTierMenuId(tierMenuId)
                .setTierServiceId(tierServiceId);
    }

}