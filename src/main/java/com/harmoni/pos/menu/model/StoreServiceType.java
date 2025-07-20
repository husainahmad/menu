package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing the association between a Store and a Service Type.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreServiceType {

    /**
     * The unique identifier of the store service type.
     */
    private Integer id;

    /**
     * The store ID associated with the service type.
     */
    private Integer storeId;

    /**
     * The store entity associated with the service type.
     */
    private Store store;

    /**
     * The sub-service ID associated with the store.
     */
    private Integer subServiceId;

    /**
     * The sub-service entity associated with the store.
     */
    private SubService subService;

    /**
     * The creation timestamp of the store service type.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the store service type.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the store service type.
     */
    private Date deletedAt;

}