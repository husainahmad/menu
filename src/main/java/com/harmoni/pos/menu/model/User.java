package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represents a user in the POS system.
 * Includes information such as username, authentication linkage,
 * and associated store.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    /** Unique identifier for the user. */
    private Integer id;

    /** Legacy role id kept for backward compatibility; use authUserId for the auth record. */
    private Integer authId;

    /** Reference to the authentication user record. */
    private Long authUserId;

    /** Username used for login or display. */
    private String username;

    /** Human-readable employee code, e.g. KSR-014. */
    private String employeeCode;

    /** Employee status: ACTIVE or INACTIVE. */
    private String status;

    /** Store ID that this user is associated with. */
    private Integer storeId;

    /** Full Store object if needed for relational data. */
    private Store store;

    /** Timestamp when the user record was created. */
    private Date createdAt;

    /** Timestamp when the user record was last updated. */
    private Date updatedAt;

    /** Timestamp when the user record was deleted (soft delete). */
    private Date deletedAt;
}
