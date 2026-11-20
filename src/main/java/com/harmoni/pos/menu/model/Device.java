package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * A device registered to a store: a POS terminal, a table-order tablet, a
 * self-order kiosk or a quick-order device.
 *
 * <p>The {@code configuration} field is a JSON object whose shape depends on
 * the {@code deviceMode}; the menu service validates it before storing.</p>
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device {

    private Integer id;
    private Integer storeId;
    private String name;
    /**
     * Operator-editable code, unique within the store.
     */
    private String address;
    /**
     * TABLET, ANDROID or DESKTOP.
     */
    private String deviceType;
    /**
     * CASHIER_POS, TABLE_ORDER, SELF_ORDER_KIOSK or QUICK_ORDER.
     */
    private String deviceMode;
    /**
     * Mode-specific JSON configuration.
     */
    private String configuration;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
