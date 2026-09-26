package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Entity representing a recurring redeemable time window of a {@link Promotion}.
 * <p>
 * A promotion with no schedule rows is treated as valid for the whole of every
 * day inside its date range. When at least one row exists the promotion is only
 * redeemable while the current local time falls inside one of the windows of the
 * current {@link #dayOfWeek} and {@link #enabled} is {@link Boolean#TRUE}.
 * <p>
 * A window whose {@link #startTime} is later than its {@link #endTime} is
 * interpreted as crossing midnight, so it also covers the early hours of the
 * following day.
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionSchedule {

    /**
     * The unique identifier of the schedule.
     */
    private Long id;

    /**
     * The promotion this schedule belongs to.
     */
    private Long promotionId;

    /**
     * The day of week this window applies to, where {@link DayOfWeek#MONDAY} is 1
     * and {@link DayOfWeek#SUNDAY} is 7.
     */
    private DayOfWeek dayOfWeek;

    /**
     * The inclusive start of the redeemable window.
     */
    private LocalTime startTime;

    /**
     * The exclusive end of the redeemable window.
     */
    private LocalTime endTime;

    /**
     * Whether the window is currently switched on. Defaults to {@code true} in the
     * database.
     */
    private Boolean enabled;
}
