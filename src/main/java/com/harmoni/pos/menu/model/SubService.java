package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a SubService, which is a child of a Service.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubService {

    /**
     * The unique identifier of the subservice.
     */
    private Integer id;

    /**
     * The service ID associated with the subservice.
     */
    private Integer serviceId;

    /**
     * The name of the subservice.
     */
    private String name;

    /**
     * The Service entity associated with the subservice.
     */
    private Service service;

    /**
     * The creation timestamp of the subservice.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the subservice.
     */
    private Date updatedAt;

}