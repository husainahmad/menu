package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a Service.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {

    /**
     * The unique identifier of the service.
     */
    private Integer id;

    /**
     * The name of the service.
     */
    private String name;

    /**
     * The list of sub-services associated with the service.
     */
    List<SubService> subServices;

    /**
     * The creation timestamp of the service.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the service.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the service.
     */
    private Date deletedAt;

}