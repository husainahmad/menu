package com.harmoni.pos.menu.model;

/**
 * Enumeration of the lifecycle states of a {@link Promotion}.
 * <p>
 * Persisted as {@code promotions.status} and defaulting to {@link #DRAFT}. A
 * promotion only becomes eligible for evaluation once it reaches
 * {@link #ACTIVE} and its date range plus {@link PromotionSchedule} windows allow it.
 */
public enum PromotionStatus {

    /** Being configured; never evaluated. */
    DRAFT,

    /** Approved and waiting for {@code start_date} to be reached. */
    SCHEDULED,

    /** Live and eligible for evaluation against the cart. */
    ACTIVE,

    /** Temporarily suspended by an operator; retains its configuration. */
    PAUSED,

    /** {@code end_date} has passed; retained for reporting. */
    EXPIRED,

    /** Terminated before its end date; retained for reporting. */
    CANCELLED
}
