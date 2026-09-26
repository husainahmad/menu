package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.PromotionSpecialPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for PromotionSpecialPrice entity database operations.
 */
@Mapper
public interface PromotionSpecialPriceMapper {

    /**
     * Deletes a PromotionSpecialPrice by its primary key.
     *
     * @param id the PromotionSpecialPrice ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every special price belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return number of rows affected
     */
    int deleteByPromotionId(Long promotionId);

    /**
     * Inserts a new PromotionSpecialPrice, replacing any existing price for the
     * same {@code (promotion_id, sku_id)} pair.
     *
     * @param row the PromotionSpecialPrice object
     * @return number of rows affected
     */
    int insertOrUpdate(PromotionSpecialPrice row);

    /**
     * Upserts several special prices in a single statement.
     *
     * @param rows the PromotionSpecialPrice objects
     * @return number of rows affected
     */
    int insertOrUpdateBatch(@Param("specialPrices") List<PromotionSpecialPrice> rows);

    /**
     * Selects a PromotionSpecialPrice by its primary key.
     *
     * @param id the PromotionSpecialPrice ID
     * @return the PromotionSpecialPrice object
     */
    PromotionSpecialPrice selectByPrimaryKey(Long id);

    /**
     * Selects a PromotionSpecialPrice by its promotion and SKU.
     *
     * @param promotionId the Promotion ID
     * @param skuId       the SKU ID
     * @return the PromotionSpecialPrice object
     */
    PromotionSpecialPrice selectByPromotionIdAndSkuId(@Param("promotionId") Long promotionId,
                                                       @Param("skuId") Long skuId);

    /**
     * Selects every special price belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return list of PromotionSpecialPrice objects
     */
    List<PromotionSpecialPrice> selectByPromotionId(Long promotionId);

    /**
     * Selects the promotional price of a SKU, preferring the most specific match:
     * the row of the promotion with the highest priority, then the lowest id.
     *
     * @param skuId the SKU ID
     * @return the PromotionSpecialPrice object, or null when the SKU has none
     */
    PromotionSpecialPrice selectEffectiveBySkuId(Long skuId);

    /**
     * Selects every special price attached to any of the given SKUs, across all
     * promotions. Callers are expected to discard the rows whose promotion is not
     * currently redeemable, which requires the owning promotion to be resolved
     * separately through {@code PromotionMapper#selectRedeemableOn}.
     *
     * @param skuIds the SKU IDs to look up
     * @return list of PromotionSpecialPrice objects
     */
    List<PromotionSpecialPrice> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * Updates a PromotionSpecialPrice by its primary key.
     *
     * @param row the PromotionSpecialPrice object
     * @return number of rows affected
     */
    int updateByPrimaryKey(PromotionSpecialPrice row);
}
