package com.harmoni.pos.menu.model.enums;

/**
 * Activation status of a store device.
 */
public enum DeviceStatus {
    ACTIVE,
    INACTIVE;

    /**
     * Parses a stored status, defaulting to {@link #ACTIVE} when absent.
     *
     * @param value the stored value
     * @return the parsed status
     */
    public static DeviceStatus from(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        return DeviceStatus.valueOf(value.trim().toUpperCase());
    }
}
