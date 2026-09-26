package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.PromotionSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for PromotionSchedule entity database operations.
 */
@Mapper
public interface PromotionScheduleMapper {

    /**
     * Deletes a PromotionSchedule by its primary key.
     *
     * @param id the PromotionSchedule ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every schedule belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return number of rows affected
     */
    int deleteByPromotionId(Long promotionId);

    /**
     * Inserts a new PromotionSchedule.
     *
     * @param row the PromotionSchedule object
     * @return number of rows affected
     */
    int insert(PromotionSchedule row);

    /**
     * Inserts several schedules in a single statement.
     *
     * @param rows the PromotionSchedule objects
     * @return number of rows affected
     */
    int insertBatch(@Param("schedules") List<PromotionSchedule> rows);

    /**
     * Selects a PromotionSchedule by its primary key.
     *
     * @param id the PromotionSchedule ID
     * @return the PromotionSchedule object
     */
    PromotionSchedule selectByPrimaryKey(Long id);

    /**
     * Selects every schedule belonging to a promotion, ordered by day then start.
     *
     * @param promotionId the Promotion ID
     * @return list of PromotionSchedule objects
     */
    List<PromotionSchedule> selectByPromotionId(Long promotionId);

    /**
     * Selects the schedules of a promotion that are enabled and whose window
     * contains the given time on the given day.
     *
     * @param promotionId the Promotion ID
     * @param dayOfWeek   the day of week to test, stored as its ordinal plus one
     * @param startTime   the inclusive start of the tested window
     * @param endTime     the exclusive end of the tested window
     * @return list of matching PromotionSchedule objects
     */
    List<PromotionSchedule> selectActiveWindow(@Param("promotionId") Long promotionId,
                                               @Param("dayOfWeek") Integer dayOfWeek,
                                               @Param("startTime") java.time.LocalTime startTime,
                                               @Param("endTime") java.time.LocalTime endTime);

    /**
     * Updates a PromotionSchedule by its primary key.
     *
     * @param row the PromotionSchedule object
     * @return number of rows affected
     */
    int updateByPrimaryKey(PromotionSchedule row);
}
