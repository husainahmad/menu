package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Mapper interface for Promotion entity database operations.
 */
@Mapper
public interface PromotionMapper {

    /**
     * Deletes a Promotion by its primary key.
     *
     * @param id the Promotion ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every Promotion matching the given filter.
     *
     * @param status     optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search     optional keyword matched against code and name
     * @return number of rows affected
     */
    int deleteByFilter(@Param("status") PromotionStatus status,
                       @Param("promotionType") PromotionType promotionType,
                       @Param("search") String search);

    /**
     * Inserts a new Promotion.
     *
     * @param row the Promotion object
     * @return number of rows affected
     */
    int insert(Promotion row);

    /**
     * Selects a Promotion by its primary key.
     *
     * @param id the Promotion ID
     * @return the Promotion object
     */
    Promotion selectByPrimaryKey(Long id);

    /**
     * Selects a Promotion by its unique code.
     *
     * @param code the Promotion code
     * @return the Promotion object
     */
    Promotion selectByCode(String code);

    /**
     * Selects every Promotion, optionally narrowed by a filter.
     *
     * @param status        optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search        optional keyword matched against code and name
     * @return list of Promotion objects
     */
    List<Promotion> selectByFilter(@Param("status") PromotionStatus status,
                                   @Param("promotionType") PromotionType promotionType,
                                   @Param("search") String search);

    /**
     * Selects the Promotions that are redeemable on the given date: a lifecycle
     * state contained in {@code statuses} (typically
     * {@link PromotionStatus#ACTIVE} and {@link PromotionStatus#SCHEDULED}), with no
     * date range or with a range that contains the date.
     *
     * @param statuses the acceptable lifecycle states
     * @param onDate   the date to test against, may be null to skip the range check
     * @return list of Promotion objects ordered by priority then code
     */
    List<Promotion> selectRedeemableOn(@Param("statuses") List<PromotionStatus> statuses,
                                       @Param("onDate") LocalDate onDate);

    /**
     * Updates a Promotion by its primary key.
     *
     * @param row the Promotion object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Promotion row);

    /**
     * Updates only the lifecycle state of a Promotion.
     *
     * @param id     the Promotion ID
     * @param status the new lifecycle state
     * @return number of rows affected
     */
    int updateStatus(@Param("id") Long id, @Param("status") PromotionStatus status);
}
