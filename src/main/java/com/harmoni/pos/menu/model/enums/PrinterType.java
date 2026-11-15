package com.harmoni.pos.menu.model.enums;

/**
 * Transport types supported by the kitchen printing engine.
 */
public enum PrinterType {
    NETWORK,
    USB,
    BLUETOOTH;

    /**
     * Parses a stored printer type.
     *
     * @param value the stored value
     * @return the matching type
     * @throws IllegalArgumentException when the value is not supported
     */
    public static PrinterType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("printerType is required");
        }
        return PrinterType.valueOf(value.trim().toUpperCase());
    }
}
