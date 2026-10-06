package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.PromotionScope;
import com.harmoni.pos.menu.model.PromotionScopeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for PromotionScope entity database operations.
 */
@Mapper
public interface PromotionScopeMapper {

    /**
     * Deletes a PromotionScope by its primary key.
     *
     * @param id the PromotionScope ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Long id);

    /**
     * Deletes every scope belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return number of rows affected
     */
    int deleteByPromotionId(Long promotionId);

    /**
     * Inserts a new PromotionScope.
     *
     * @param row the PromotionScope object
     * @return number of rows affected
     */
    int insert(PromotionScope row);

    /**
     * Inserts several scopes in a single statement.
     *
     * @param rows the PromotionScope objects
     * @return number of rows affected
     */
    int insertBatch(@Param("scopes") List<PromotionScope> rows);

    /**
     * Selects a PromotionScope by its primary key.
     *
     * @param id the PromotionScope ID
     * @return the PromotionScope object
     */
    PromotionScope selectByPrimaryKey(Long id);

    /**
     * Selects every scope belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @return list of PromotionScope objects
     */
    List<PromotionScope> selectByPromotionId(Long promotionId);

    /**
     * Selects every scope belonging to any of the given promotions in one round trip.
     * Used by the promotion engine to load the whole candidate set without issuing
     * one query per promotion.
     *
     * @param promotionIds the Promotion IDs to load, must not be empty
     * @return list of PromotionScope objects
     */
    List<PromotionScope> selectByPromotionIds(@Param("promotionIds") List<Long> promotionIds);

    /**
     * Selects every scope of a given type belonging to a promotion.
     *
     * @param promotionId the Promotion ID
     * @param scopeType the scope type
     * @return list of PromotionScope objects
     */
    List<PromotionScope> selectByPromotionIdAndType(@Param("promotionId") Long promotionId,
                                                     @Param("scopeType") PromotionScopeType scopeType);

    /**
     * Selects the scope rows that cover any of the supplied entity references.
     * Used to resolve which promotions apply to a tenant/brand/chain/store in one round trip.
     *
     * @param promotionIds the Promotion IDs to restrict the result to, may be null
     * @param scopeType the scope type to filter by, may be null
     * @param scopeIds   the scope IDs to match, may be null
     * @return list of PromotionScope objects
     */
    List<PromotionScope> selectByEntityRefs(@Param("promotionIds") List<Long> promotionIds,
                                            @Param("scopeType") PromotionScopeType scopeType,
                                            @Param("scopeIds") List<Long> scopeIds);

    /**
     * Updates a PromotionScope by its primary key.
     *
     * @param row the PromotionScope object
     * @return number of rows affected
     */
    int updateByPrimaryKey(PromotionScope row);
}