package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.PromotionSchedule;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Data transfer object for a PromotionSchedule.
 */
@Data
public class PromotionScheduleDto {

    @NotNull(message = "{validation.promotionSchedule.dayOfWeek.NotNull}")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "{validation.promotionSchedule.startTime.NotNull}")
    private LocalTime startTime;

    @NotNull(message = "{validation.promotionSchedule.endTime.NotNull}")
    private LocalTime endTime;

    private Boolean enabled;

    /**
     * Converts this DTO to a PromotionSchedule entity.
     *
     * @return a PromotionSchedule entity, promotionId left unset
     */
    public PromotionSchedule toPromotionSchedule() {
        return new PromotionSchedule()
                .setDayOfWeek(dayOfWeek)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setEnabled(enabled == null || enabled);
    }
}
