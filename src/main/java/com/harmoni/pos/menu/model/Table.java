package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Table (dining table) in a store.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Table {

    /**
     * The unique identifier of the table.
     */
    private Integer id;

    /**
     * The store ID associated with the table.
     */
    private Integer storeId;

    /**
     * The store entity associated with the table.
     */
    private Store store;

    /**
     * The name of the table.
     */
    private String name;

    /**
     * The seating capacity of the table.
     */
    private Integer capacity;

    /**
     * The creation timestamp of the table.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the table.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the table.
     */
    private Date deletedAt;

}