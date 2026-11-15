package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Brand;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Brand.
 * Used for transferring brand data between layers.
 */
@Data
public class BrandDto {

    /**
     * The name of the brand.
     */
    @NotBlank(message = "{validation.brand.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * The hosted URL of the brand logo.
     */
    private String logoUrl;

    /**
     * The IANA time zone of the brand.
     */
    private String timeZone;

    /**
     * The ISO currency code of the brand.
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
     * Converts this DTO to a Brand entity.
     *
     * @return a Brand entity with the fields set
     */
    public Brand toBrand() {
        return new Brand()
                .setName(name)
                .setLogoUrl(logoUrl)
                .setTimeZone(timeZone == null ? "Asia/Jakarta" : timeZone)
                .setCurrency(currency == null ? "IDR" : currency)
                .setAdminEmail(adminEmail)
                .setInitialMenuMode(initialMenuMode == null ? "EMPTY" : initialMenuMode)
                .setSourceBrandId(sourceBrandId);
    }
}
