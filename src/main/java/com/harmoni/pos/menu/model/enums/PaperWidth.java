package com.harmoni.pos.menu.model.enums;

/**
 * Paper widths supported by the kitchen printing engine.
 *
 * <p>The character count is the number of monospaced characters that fit on one
 * line at the default ESC/POS font. 58mm is 32 columns, 80mm is 48 columns.</p>
 */
public enum PaperWidth {
    MM58("58MM"),
    MM80("80MM");

    private final String code;

    PaperWidth(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Parses a stored paper width code such as {@code 58MM}.
     *
     * @param value the stored value
     * @return the matching paper width
     * @throws IllegalArgumentException when the value is not supported
     */
    public static PaperWidth fromCode(String value) {
        if (value == null || value.isBlank()) {
            return MM80;
        }
        for (PaperWidth width : values()) {
            if (width.code.equalsIgnoreCase(value.trim())) {
                return width;
            }
        }
        throw new IllegalArgumentException("Unsupported paper width: " + value);
    }
}
