package com.harmoni.pos.menu.model.enums;

/**
 * Operating mode of a store device. The mode decides the shape of the device's
 * {@code configuration} JSON.
 */
public enum DeviceMode {
    CASHIER_POS,
    TABLE_ORDER,
    SELF_ORDER_KIOSK,
    QUICK_ORDER;

    /**
     * Parses a stored device mode.
     *
     * @param value the stored value
     * @return the matching mode
     * @throws IllegalArgumentException when the value is not supported
     */
    public static DeviceMode from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("deviceMode is required");
        }
        return DeviceMode.valueOf(value.trim().toUpperCase());
    }
}
