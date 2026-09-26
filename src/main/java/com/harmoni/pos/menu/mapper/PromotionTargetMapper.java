package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.PromotionTarget;
import com.harmoni.pos.menu.model.PromotionTargetType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for PromotionTarget entity database operations.
 */
@Mapper
public interface PromotionTargetMapper {

    /**
     * Deletes a PromotionTarget by its primary key.
     *
     * @param id the PromotionTarget ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every target belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return number of rows affected
     */
    int deleteByPromotionId(Long promotionId);

    /**
     * Inserts a new PromotionTarget.
     *
     * @param row the PromotionTarget object
     * @return number of rows affected
     */
    int insert(PromotionTarget row);

    /**
     * Inserts several targets in a single statement.
     *
     * @param rows the PromotionTarget objects
     * @return number of rows affected
     */
    int insertBatch(@Param("targets") List<PromotionTarget> rows);

    /**
     * Selects a PromotionTarget by its primary key.
     *
     * @param id the PromotionTarget ID
     * @return the PromotionTarget object
     */
    PromotionTarget selectByPrimaryKey(Long id);

    /**
     * Selects every target belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return list of PromotionTarget objects
     */
    List<PromotionTarget> selectByPromotionId(Long promotionId);

    /**
     * Selects every target of a given type belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @param targetType the target type
     * @return list of PromotionTarget objects
     */
    List<PromotionTarget> selectByPromotionIdAndType(@Param("promotionId") Long promotionId,
                                                     @Param("targetType") PromotionTargetType targetType);

    /**
     * Selects the target rows that cover any of the supplied catalog references.
     * Used to resolve which promotions apply to a cart in one round trip.
     *
     * @param promotionIds the Promotion IDs to restrict the result to, may be null
     * @param productIds   the product IDs to match, may be null
     * @param skuIds       the SKU IDs to match, may be null
     * @param categoryIds  the category IDs to match, may be null
     * @return list of PromotionTarget objects
     */
    List<PromotionTarget> selectByCatalogRefs(@Param("promotionIds") List<Long> promotionIds,
                                              @Param("productIds") List<Long> productIds,
                                              @Param("skuIds") List<Long> skuIds,
                                              @Param("categoryIds") List<Long> categoryIds);

    /**
     * Updates a PromotionTarget by its primary key.
     *
     * @param row the PromotionTarget object
     * @return number of rows affected
     */
    int updateByPrimaryKey(PromotionTarget row);
}
