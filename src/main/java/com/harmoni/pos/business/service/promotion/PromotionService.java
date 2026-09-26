package com.harmoni.pos.business.service.promotion;

import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;

import java.util.List;
import java.util.Map;

/**
 * Business service for managing the promotion aggregate.
 */
public interface PromotionService {

    /**
     * Creates a promotion together with its schedules, targets, rules and special
     * prices.
     *
     * @param promotionAddDto the DTO carrying the whole aggregate
     * @return the created promotion with its child collections populated
     */
    Promotion create(PromotionAddDto promotionAddDto);

    /**
     * Updates a promotion. A child collection that is omitted from the request is
     * left untouched; one submitted as an empty list is cleared.
     *
     * @param promotionEditDto the DTO carrying the whole aggregate
     * @return the updated promotion with its child collections populated
     */
    Promotion update(PromotionEditDto promotionEditDto);

    /**
     * Deletes a promotion and, by cascade, its schedules, targets, rules and
     * special prices. Already recorded order item discounts are deliberately left
     * untouched so historic orders stay intact.
     *
     * @param id the promotion ID
     * @return the number of deleted rows
     */
    int delete(Long id);

    /**
     * Deletes every promotion matching the given filter.
     *
     * @param status        optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search        optional keyword matched against code and name
     * @return the number of deleted rows
     */
    int deleteByFilter(PromotionStatus status, PromotionType promotionType, String search);

    /**
     * Retrieves a promotion by its ID, with all child collections populated.
     *
     * @param id the promotion ID
     * @return the promotion
     */
    Promotion get(Long id);

    /**
     * Retrieves a promotion by its unique code, without child collections.
     *
     * @param code the promotion code
     * @return the promotion
     */
    Promotion getByCode(String code);

    /**
     * Retrieves a paginated, optionally filtered list of promotions. The child
     * collections are not populated to keep the page payload small.
     *
     * @param status        optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search        optional keyword matched against code and name
     * @param page          the 1-based page number
     * @param size          the page size
     * @return a map holding page, size, total, data and navigate
     */
    Map<String, Object> listPaginated(PromotionStatus status, PromotionType promotionType,
                                      String search, int page, int size);

    /**
     * Retrieves the promotions that may be redeemed right now, that is whose status
     * is ACTIVE or SCHEDULED and whose date range contains today. Callers must
     * still check the schedules of each promotion against the current time.
     *
     * @return list of promotions ordered by priority
     */
    List<Promotion> listRedeemable();

    /**
     * Moves a promotion to a new lifecycle state.
     *
     * @param id     the promotion ID
     * @param status the new lifecycle state
     * @return the number of updated rows
     */
    int updateStatus(Long id, PromotionStatus status);
}
