package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Brand.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Brand {

    /**
     * The unique identifier of the brand.
     */
    private Integer id;

    /**
     * The name of the brand.
     */
    private String name;

    /**
     * The hosted URL of the brand logo, used on receipts and the POS app.
     */
    private String logoUrl;

    /**
     * The IANA time zone of the brand, e.g. {@code Asia/Jakarta}.
     */
    private String timeZone;

    /**
     * The ISO currency code of the brand, e.g. {@code IDR}.
     */
    private String currency;

    /**
     * The email address of the brand administrator.
     */
    private String adminEmail;

    /**
     * How the initial menu is created: {@code EMPTY} or {@code COPY}.
     */
    private String initialMenuMode;

    /**
     * The brand whose menu is copied when {@link #initialMenuMode} is {@code COPY}.
     */
    private Integer sourceBrandId;

    /**
     * The creation timestamp of the brand.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the brand.
     */
    private Date updatedAt;

}