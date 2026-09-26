package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.PromotionRule;
import com.harmoni.pos.menu.model.PromotionRuleType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for PromotionRule entity database operations.
 */
@Mapper
public interface PromotionRuleMapper {

    /**
     * Deletes a PromotionRule by its primary key.
     *
     * @param id the PromotionRule ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every rule belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return number of rows affected
     */
    int deleteByPromotionId(Long promotionId);

    /**
     * Inserts a new PromotionRule.
     *
     * @param row the PromotionRule object
     * @return number of rows affected
     */
    int insert(PromotionRule row);

    /**
     * Inserts several rules in a single statement.
     *
     * @param rows the PromotionRule objects
     * @return number of rows affected
     */
    int insertBatch(@Param("rules") List<PromotionRule> rows);

    /**
     * Selects a PromotionRule by its primary key.
     *
     * @param id the PromotionRule ID
     * @return the PromotionRule object
     */
    PromotionRule selectByPrimaryKey(Long id);

    /**
     * Selects every rule belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return list of PromotionRule objects
     */
    List<PromotionRule> selectByPromotionId(Long promotionId);

    /**
     * Selects the rules of a given type belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @param ruleType    the rule type
     * @return list of PromotionRule objects
     */
    List<PromotionRule> selectByPromotionIdAndType(@Param("promotionId") Long promotionId,
                                                   @Param("ruleType") PromotionRuleType ruleType);

    /**
     * Updates a PromotionRule by its primary key.
     *
     * @param row the PromotionRule object
     * @return number of rows affected
     */
    int updateByPrimaryKey(PromotionRule row);
}
