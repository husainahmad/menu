package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * A printer attached to a kitchen station. The primary printer is the one chosen
 * when a ticket for the station is created.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenPrinter {

    private Integer id;
    private Integer kitchenStationId;
    private String name;
    /**
     * NETWORK, USB or BLUETOOTH.
     */
    private String printerType;
    /**
     * JSON transport configuration. Network: host/port/timeoutMs. USB: device or
     * CUPS queue. Bluetooth: serial device path.
     */
    private String connectionConfig;
    /**
     * 58MM or 80MM.
     */
    private String paperWidth;
    private Boolean isPrimary;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    /**
     * Station name, populated by joined list queries.
     */
    private String stationName;
}
