package com.harmoni.pos.business.service.promotion.engine;

import com.harmoni.pos.menu.model.PromotionSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Decides whether a promotion is redeemable at a given wall-clock time.
 * <p>
 * Pure and static so the window arithmetic, which is the part of a promotion system
 * most likely to be subtly wrong, can be tested exhaustively without a Spring context
 * or a datasource.
 * <p>
 * The semantics implemented here are the ones documented on
 * {@link PromotionSchedule}:
 * <ul>
 *   <li>A promotion with no schedule rows is valid for the whole of every day inside
 *       its date range.</li>
 *   <li>Otherwise the current time must fall inside one of the enabled windows of the
 *       current day of week.</li>
 *   <li>{@code startTime} is inclusive and {@code endTime} is exclusive.</li>
 *   <li>A window whose {@code startTime} is later than its {@code endTime} crosses
 *       midnight, so it also covers the early hours of the following day. A Friday
 *       {@code 23:00-01:00} window therefore matches at 00:30 on Saturday, even
 *       though {@link DayOfWeek#SATURDAY} has no row of its own for it.</li>
 * </ul>
 *
 * @author husainahmad
 */
public final class PromotionScheduleMatcher {

    private PromotionScheduleMatcher() {
    }

    /**
     * Whether the promotion may be redeemed at the given instant.
     *
     * @param schedules the promotion's schedules, may be null or empty
     * @param dayOfWeek the local day of week at the evaluation instant
     * @param timeOfDay the local time of day at the evaluation instant
     * @return true when the promotion is redeemable
     */
    public static boolean isRedeemable(List<PromotionSchedule> schedules,
                                       DayOfWeek dayOfWeek,
                                       LocalTime timeOfDay) {
        if (schedules == null || schedules.isEmpty()) {
            return true;
        }
        DayOfWeek previousDay = dayOfWeek == null ? null : dayOfWeek.minus(1);
        for (PromotionSchedule schedule : schedules) {
            if (isDisabled(schedule)) {
                continue;
            }
            if (covers(schedule, dayOfWeek, previousDay, timeOfDay)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A window is only consulted on its own day of week, except for the early hours it
     * spills into, which belong to the previous day's row.
     */
    private static boolean covers(PromotionSchedule schedule,
                                  DayOfWeek dayOfWeek,
                                  DayOfWeek previousDay,
                                  LocalTime timeOfDay) {
        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        if (start == null || end == null || timeOfDay == null) {
            return false;
        }
        if (isSameDay(schedule.getDayOfWeek(), dayOfWeek)) {
            return start.isAfter(end)
                    ? timeOfDay.compareTo(start) >= 0
                    : withinSameDay(start, end, timeOfDay);
        }
        if (start.isAfter(end) && isSameDay(schedule.getDayOfWeek(), previousDay)) {
            return timeOfDay.compareTo(end) < 0;
        }
        return false;
    }

    private static boolean withinSameDay(LocalTime start, LocalTime end, LocalTime timeOfDay) {
        return timeOfDay.compareTo(start) >= 0 && timeOfDay.compareTo(end) < 0;
    }

    private static boolean isSameDay(DayOfWeek scheduled, DayOfWeek actual) {
        return scheduled != null && scheduled == actual;
    }

    /**
     * A null {@code enabled} is treated as enabled. The column defaults to true and the
     * entity documents that, so a null can only come from a hand-written row; failing
     * closed there would silently switch off a promotion an operator can see as live.
     */
    private static boolean isDisabled(PromotionSchedule schedule) {
        return Boolean.FALSE.equals(schedule.getEnabled());
    }
}
