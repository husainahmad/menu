package com.harmoni.pos.menu.model.enums;

/**
 * Activation status shared by kitchen stations and printers.
 */
public enum KitchenStatus {
    ACTIVE,
    INACTIVE;

    /**
     * Parses a stored status, defaulting to {@link #ACTIVE} when absent.
     *
     * @param value the stored value
     * @return the parsed status
     */
    public static KitchenStatus from(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        return KitchenStatus.valueOf(value.trim().toUpperCase());
    }
}
