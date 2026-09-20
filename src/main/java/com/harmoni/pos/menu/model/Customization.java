package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
/*
 * Represents a customization option for a menu item.
 */
public class Customization {
    /**
     * Unique identifier for the customization.
     */
    private Integer id;

    /**
     * Name of the customization.
     */
    private String name;

    /**
     * Description of the customization.
     */
    private String description;

    /**
     * Minimum number of options that must be selected.
     */
    private Integer minimumSelection;

    /**
     * Maximum number of options that can be selected.
     */
    private Integer maximumSelection;

    /**
     * Type of selection (e.g., single, multiple). Use Enum if needed.
     */
    private SelectionType selectionType;

    /**
     * Identifier for the associated brand.
     */
    private Integer brandId;

    /**
     * Indicates if the customization is deleted.
     */
    private Boolean isDeleted;

    /**
     * Indicates if the customization is required.
     */
    private Boolean required;

    /**
     * Timestamp when the customization was created.
     */
    private Date createdAt;

    /**
     * Timestamp when the customization was last updated.
     */
    private Date updatedAt;

    /**
     * Timestamp when the customization was deleted.
     */
    private Date deletedAt;

    private List<CustomizationOption> customizationOptions;
}

