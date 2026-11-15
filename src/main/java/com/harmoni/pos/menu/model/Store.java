package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity representing a Store.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Store {

    /**
     * The unique identifier of the store.
     */
    private Integer id;

    /**
     * The name of the store.
     */
    private String name;

    /**
     * The chain ID associated with the store.
     */
    private Integer chainId;

    /**
     * The chain entity associated with the store.
     */
    private Chain chain;

    /**
     * The address of the store.
     */
    private String address;

    /**
     * The order types enabled at the store, comma separated:
     * {@code DINE_IN,TAKEAWAY,DELIVERY}.
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
     * The time zone of the store.
     */
    private String timeZone;

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
     * The creation timestamp of the store.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the store.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the store.
     */
    private Date deletedAt;

}