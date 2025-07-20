package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

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