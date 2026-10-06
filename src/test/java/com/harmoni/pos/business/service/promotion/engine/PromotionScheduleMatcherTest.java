package com.harmoni.pos.business.service.promotion.engine;

import com.harmoni.pos.menu.model.PromotionSchedule;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionScheduleMatcherTest {

    private static PromotionSchedule window(DayOfWeek day, String from, String to, Boolean enabled) {
        return new PromotionSchedule()
                .setDayOfWeek(day)
                .setStartTime(LocalTime.parse(from))
                .setEndTime(LocalTime.parse(to))
                .setEnabled(enabled);
    }

    @Test
    void noSchedules_shouldBeRedeemableAllDay() {
        assertTrue(PromotionScheduleMatcher.isRedeemable(null, DayOfWeek.WEDNESDAY, LocalTime.parse("03:00")));
        assertTrue(PromotionScheduleMatcher.isRedeemable(List.of(), DayOfWeek.WEDNESDAY, LocalTime.parse("03:00")));
    }

    @Test
    void sameDayWindow_shouldMatchBetweenInclusiveStartAndExclusiveEnd() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.MONDAY, "15:00", "17:00", true));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("15:00")));
        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("16:59")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("17:00")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("14:59")));
    }

    @Test
    void sameDayWindow_shouldNotMatchAnotherDay() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.MONDAY, "15:00", "17:00", true));

        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.TUESDAY, LocalTime.parse("16:00")));
    }

    @Test
    void disabledWindow_shouldNeverMatch() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.MONDAY, "00:00", "23:59", false));

        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("12:00")));
    }

    @Test
    void nullEnabled_shouldBeTreatedAsEnabled() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.MONDAY, "09:00", "17:00", null));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("12:00")));
    }

    @Test
    void midnightCrossingWindow_shouldMatchLateOnItsOwnDay() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.FRIDAY, "23:00", "01:00", true));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.FRIDAY, LocalTime.parse("23:30")));
        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.FRIDAY, LocalTime.parse("23:00")));
    }

    @Test
    void midnightCrossingWindow_shouldMatchEarlyHoursOfTheFollowingDay() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.FRIDAY, "23:00", "01:00", true));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.SATURDAY, LocalTime.parse("00:30")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.SATURDAY, LocalTime.parse("01:00")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.SATURDAY, LocalTime.parse("12:00")));
    }

    @Test
    void midnightCrossingWindow_shouldWrapOverSundayToMonday() {
        List<PromotionSchedule> schedules =
                List.of(window(DayOfWeek.SUNDAY, "22:00", "02:00", true));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("01:00")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("03:00")));
    }

    @Test
    void anyOfSeveralEnabledWindowsMatching_shouldBeRedeemable() {
        List<PromotionSchedule> schedules = List.of(
                window(DayOfWeek.MONDAY, "09:00", "12:00", true),
                window(DayOfWeek.MONDAY, "15:00", "17:00", true));

        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("10:00")));
        assertTrue(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("16:00")));
        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("13:00")));
    }

    @Test
    void allWindowsDisabledOrOtherDay_shouldNotBeRedeemable() {
        List<PromotionSchedule> schedules = List.of(
                window(DayOfWeek.MONDAY, "09:00", "12:00", false),
                window(DayOfWeek.TUESDAY, "09:00", "12:00", true));

        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("10:00")));
    }

    @Test
    void windowMissingTimes_shouldNotMatch() {
        List<PromotionSchedule> schedules = List.of(new PromotionSchedule()
                .setDayOfWeek(DayOfWeek.MONDAY)
                .setEnabled(true));

        assertFalse(PromotionScheduleMatcher.isRedeemable(schedules, DayOfWeek.MONDAY, LocalTime.parse("10:00")));
    }
}
