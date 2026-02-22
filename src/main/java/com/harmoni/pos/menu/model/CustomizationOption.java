package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represents an option under a specific customization.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomizationOption {
    private Integer id;
    private Integer customizationId;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean isDeleted;
}
