package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a floor of a store. A floor groups the dining tables of
 * one store, for example "Ground", "Mezzanine" or "Terrace".
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Floor {

    /**
     * The unique identifier of the floor.
     */
    private Integer id;

    /**
     * The store this floor belongs to.
     */
    private Integer storeId;

    /**
     * The store entity associated with the floor.
     */
    private Store store;

    /**
     * The display name of the floor.
     */
    private String name;

    /**
     * Order in which floors are listed, ascending.
     */
    private Integer sortOrder;

    /**
     * Whether the floor is active. Inactive floors stay in history but are
     * hidden from the ordering screens.
     */
    private Boolean active;

    /**
     * The creation timestamp of the floor.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the floor.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the floor.
     */
    private Date deletedAt;
}
