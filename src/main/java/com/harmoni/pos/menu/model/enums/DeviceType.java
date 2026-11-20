package com.harmoni.pos.menu.model.enums;

/**
 * Hardware type of a store device.
 */
public enum DeviceType {
    TABLET,
    ANDROID,
    DESKTOP;

    /**
     * Parses a stored device type.
     *
     * @param value the stored value
     * @return the matching type
     * @throws IllegalArgumentException when the value is not supported
     */
    public static DeviceType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("deviceType is required");
        }
        return DeviceType.valueOf(value.trim().toUpperCase());
    }
}
