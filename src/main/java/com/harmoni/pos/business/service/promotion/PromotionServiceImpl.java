package com.harmoni.pos.business.service.promotion;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.mapper.PromotionMapper;
import com.harmoni.pos.menu.mapper.PromotionRuleMapper;
import com.harmoni.pos.menu.mapper.PromotionScheduleMapper;
import com.harmoni.pos.menu.mapper.PromotionSpecialPriceMapper;
import com.harmoni.pos.menu.mapper.PromotionTargetMapper;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionRule;
import com.harmoni.pos.menu.model.PromotionSchedule;
import com.harmoni.pos.menu.model.PromotionSpecialPrice;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionTarget;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.PromotionRuleDto;
import com.harmoni.pos.menu.model.dto.PromotionScheduleDto;
import com.harmoni.pos.menu.model.dto.PromotionSpecialPriceDto;
import com.harmoni.pos.menu.model.dto.PromotionTargetDto;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link PromotionService}.
 * <p>
 * Owns the lifecycle of the promotion aggregate. Every write goes through a single
 * transaction against the primary menu datasource, so a failure half way through
 * the child collections cannot leave a promotion header without its targets.
 *
 * @author husainahmad
 */
@RequiredArgsConstructor
@Service("promotionService")
@Slf4j
public class PromotionServiceImpl implements PromotionService {

    private static final List<PromotionStatus> REDEEMABLE_STATUSES =
            List.of(PromotionStatus.ACTIVE, PromotionStatus.SCHEDULED);

    private final PromotionMapper promotionMapper;
    private final PromotionScheduleMapper promotionScheduleMapper;
    private final PromotionTargetMapper promotionTargetMapper;
    private final PromotionRuleMapper promotionRuleMapper;
    private final PromotionSpecialPriceMapper promotionSpecialPriceMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public Promotion create(PromotionAddDto promotionAddDto) {
        String code = promotionAddDto.getCode();
        if (!ObjectUtils.isEmpty(promotionMapper.selectByCode(code))) {
            throw new BusinessBadRequestException("exception.promotion.badRequest.duplicate", new Object[]{code});
        }
        validate(promotionAddDto.toPromotion(), promotionAddDto.getTargetDtos());

        Promotion promotion = promotionAddDto.toPromotion();
        Date now = new Date();
        promotion.setCreatedAt(now)
                .setUpdatedAt(now);
        if (promotionMapper.insert(promotion) < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }

        insertSchedules(promotion.getId(), promotionAddDto.getScheduleDtos());
        insertTargets(promotion.getId(), promotionAddDto.getTargetDtos());
        insertRules(promotion.getId(), promotionAddDto.getRuleDtos());
        upsertSpecialPrices(promotion.getId(), promotionAddDto.getSpecialPriceDtos());

        return attach(promotion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public Promotion update(PromotionEditDto promotionEditDto) {
        Promotion existing = this.getHeader(promotionEditDto.getId());
        Promotion promotion = promotionEditDto.toPromotion();
        validate(promotion, promotionEditDto.getTargetDtos());

        String code = promotion.getCode();
        Promotion byCode = promotionMapper.selectByCode(code);
        if (!ObjectUtils.isEmpty(byCode) && !byCode.getId().equals(existing.getId())) {
            throw new BusinessBadRequestException("exception.promotion.badRequest.duplicate", new Object[]{code});
        }

        promotion.setId(existing.getId())
                .setCreatedAt(existing.getCreatedAt())
                .setUpdatedAt(new Date());
        if (promotionMapper.updateByPrimaryKey(promotion) < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }

        Long promotionId = promotion.getId();
        if (promotionEditDto.getScheduleDtos() != null) {
            promotionScheduleMapper.deleteByPromotionId(promotionId);
            insertSchedules(promotionId, promotionEditDto.getScheduleDtos());
        }
        if (promotionEditDto.getTargetDtos() != null) {
            promotionTargetMapper.deleteByPromotionId(promotionId);
            insertTargets(promotionId, promotionEditDto.getTargetDtos());
        }
        if (promotionEditDto.getRuleDtos() != null) {
            promotionRuleMapper.deleteByPromotionId(promotionId);
            insertRules(promotionId, promotionEditDto.getRuleDtos());
        }
        if (promotionEditDto.getSpecialPriceDtos() != null) {
            promotionSpecialPriceMapper.deleteByPromotionId(promotionId);
            upsertSpecialPrices(promotionId, promotionEditDto.getSpecialPriceDtos());
        }

        return attach(promotion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public int delete(Long id) {
        Promotion promotion = this.get(id);
        Long promotionId = promotion.getId();
        promotionScheduleMapper.deleteByPromotionId(promotionId);
        promotionTargetMapper.deleteByPromotionId(promotionId);
        promotionRuleMapper.deleteByPromotionId(promotionId);
        promotionSpecialPriceMapper.deleteByPromotionId(promotionId);
        return promotionMapper.deleteByPrimaryKey(promotionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public int deleteByFilter(PromotionStatus status, PromotionType promotionType, String search) {
        List<Promotion> promotions = promotionMapper.selectByFilter(status, promotionType, search);
        promotions.forEach(promotion -> {
            Long promotionId = promotion.getId();
            promotionScheduleMapper.deleteByPromotionId(promotionId);
            promotionTargetMapper.deleteByPromotionId(promotionId);
            promotionRuleMapper.deleteByPromotionId(promotionId);
            promotionSpecialPriceMapper.deleteByPromotionId(promotionId);
        });
        return promotionMapper.deleteByFilter(status, promotionType, search);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion get(Long id) {
        return attach(this.getHeader(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion getByCode(String code) {
        Promotion promotion = promotionMapper.selectByCode(code);
        if (ObjectUtils.isEmpty(promotion)) {
            throw new BusinessBadRequestException("exception.promotion.code.badRequest.notFound", new Object[]{code});
        }
        return promotion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> listPaginated(PromotionStatus status, PromotionType promotionType,
                                             String search, int page, int size) {
        PaginationUtils.applyPagination(page, size);
        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<Promotion> promotionPageInfo =
                new PageInfo<>(promotionMapper.selectByFilter(status, promotionType, normalize(search)));

        paginationData.put("page", promotionPageInfo.getPages());
        paginationData.put("size", promotionPageInfo.getSize());
        paginationData.put("total", promotionPageInfo.getTotal());
        paginationData.put("data", promotionPageInfo.getList());
        paginationData.put("navigate", promotionPageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> listRedeemable() {
        return promotionMapper.selectRedeemableOn(REDEEMABLE_STATUSES, LocalDate.now());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateStatus(Long id, PromotionStatus status) {
        this.getHeader(id);
        return promotionMapper.updateStatus(id, status);
    }

    /**
     * Loads a promotion header without any child collection, failing when absent.
     *
     * @param id the promotion ID
     * @return the promotion header
     */
    private Promotion getHeader(Long id) {
        Promotion promotion = promotionMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(promotion)) {
            throw new BusinessBadRequestException("exception.promotion.id.badRequest.notFound", new Object[]{id});
        }
        return promotion;
    }

    /**
     * Populates the four child collections of a promotion.
     *
     * @param promotion the promotion to populate
     * @return the same promotion instance, for chaining
     */
    private Promotion attach(Promotion promotion) {
        Long promotionId = promotion.getId();
        List<PromotionSchedule> schedules = promotionScheduleMapper.selectByPromotionId(promotionId);
        List<PromotionTarget> targets = promotionTargetMapper.selectByPromotionId(promotionId);
        List<PromotionRule> rules = promotionRuleMapper.selectByPromotionId(promotionId);
        List<PromotionSpecialPrice> specialPrices = promotionSpecialPriceMapper.selectByPromotionId(promotionId);
        return promotion.setSchedules(schedules)
                .setTargets(targets)
                .setRules(rules)
                .setSpecialPrices(specialPrices);
    }

    /**
     * Rejects a promotion that could never be redeemed, or whose target rows are
     * internally inconsistent.
     *
     * @param promotion   the promotion to check
     * @param targetDtos  the submitted targets, may be null
     */
    private void validate(Promotion promotion, List<PromotionTargetDto> targetDtos) {
        LocalDate startDate = promotion.getStartDate();
        LocalDate endDate = promotion.getEndDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessBadRequestException("exception.promotion.badRequest.invalidDateRange", null);
        }
        if (promotion.getPromotionType() == PromotionType.SPECIAL_PRICE
                && ObjectUtils.isEmpty(promotion.getStatus())) {
            throw new BusinessBadRequestException("exception.promotion.badRequest.statusRequired", null);
        }
        if (ObjectUtils.isEmpty(targetDtos)) {
            return;
        }
        targetDtos.forEach(target -> {
            if (ObjectUtils.isEmpty(target.getTargetType())) {
                throw new BusinessBadRequestException("exception.promotionTarget.badRequest.targetTypeRequired", null);
            }
            boolean referencePresent = switch (target.getTargetType()) {
                case PRODUCT -> target.getProductId() != null;
                case SKU -> target.getSkuId() != null;
                case CATEGORY -> target.getCategoryId() != null;
            };
            if (!referencePresent) {
                throw new BusinessBadRequestException("exception.promotionTarget.badRequest.referenceRequired",
                        new Object[]{target.getTargetType()});
            }
        });
    }

    /**
     * Inserts the schedules of a promotion in one statement.
     *
     * @param promotionId the promotion ID
     * @param dtos        the schedule DTOs, may be null or empty
     */
    private void insertSchedules(Long promotionId, List<PromotionScheduleDto> dtos) {
        if (ObjectUtils.isEmpty(dtos)) {
            return;
        }
        List<PromotionSchedule> schedules = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> {
            PromotionSchedule schedule = dto.toPromotionSchedule();
            schedule.setPromotionId(promotionId);
            schedules.add(schedule);
        });
        promotionScheduleMapper.insertBatch(schedules);
    }

    /**
     * Inserts the targets of a promotion in one statement.
     *
     * @param promotionId the promotion ID
     * @param dtos        the target DTOs, may be null or empty
     */
    private void insertTargets(Long promotionId, List<PromotionTargetDto> dtos) {
        if (ObjectUtils.isEmpty(dtos)) {
            return;
        }
        List<PromotionTarget> targets = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> {
            PromotionTarget target = dto.toPromotionTarget();
            target.setPromotionId(promotionId);
            targets.add(target);
        });
        promotionTargetMapper.insertBatch(targets);
    }

    /**
     * Inserts the rules of a promotion in one statement.
     *
     * @param promotionId the promotion ID
     * @param dtos        the rule DTOs, may be null or empty
     */
    private void insertRules(Long promotionId, List<PromotionRuleDto> dtos) {
        if (ObjectUtils.isEmpty(dtos)) {
            return;
        }
        List<PromotionRule> rules = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> {
            PromotionRule rule = dto.toPromotionRule();
            rule.setPromotionId(promotionId);
            rules.add(rule);
        });
        promotionRuleMapper.insertBatch(rules);
    }

    /**
     * Upserts the per-SKU special prices of a promotion in one statement.
     *
     * @param promotionId the promotion ID
     * @param dtos        the special price DTOs, may be null or empty
     */
    private void upsertSpecialPrices(Long promotionId, List<PromotionSpecialPriceDto> dtos) {
        if (ObjectUtils.isEmpty(dtos)) {
            return;
        }
        List<PromotionSpecialPrice> specialPrices = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> {
            PromotionSpecialPrice specialPrice = dto.toPromotionSpecialPrice();
            specialPrice.setPromotionId(promotionId);
            specialPrices.add(specialPrice);
        });
        promotionSpecialPriceMapper.insertOrUpdateBatch(specialPrices);
    }

    /**
     * Trims a search term and collapses a blank one to null so the mapper can treat
     * it as "no filter".
     *
     * @param search the raw search term
     * @return the normalized search term, or null
     */
    private static String normalize(String search) {
        return search == null || search.isBlank() ? null : search.trim();
    }
}
