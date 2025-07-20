package com.harmoni.pos.menu.model;

/**
 * Enumeration of possible tier types used in the system.
 * <p>
 * Tier types help define different business logic layers such as pricing strategies,
 * menu availability, or service levels.
 */
public enum TierType {

    /** Tier used for price segmentation (e.g., wholesale, retail). */
    PRICE,

    /** Tier used to control menu availability or customization per tier. */
    MENU,

    /** Tier used to manage service levels or offerings based on tier. */
    SERVICE
}
