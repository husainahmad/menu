package com.harmoni.pos.business.service.promotion.orderitemdiscount;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.OrderItemDiscountMapper;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.OrderItemDiscount;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.dto.OrderItemDiscountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link OrderItemDiscountService}.
 * <p>
 * The rows written here are an immutable audit trail: once a discount is snapshotted
 * it is never mutated, only added to or removed with its order line. The promotion
 * code and name are denormalised onto the row so historic orders remain readable
 * after a promotion is renamed or deleted.
 *
 * @author husainahmad
 */
@RequiredArgsConstructor
@Service("orderItemDiscountService")
@Slf4j
public class OrderItemDiscountServiceImpl implements OrderItemDiscountService {

    private final OrderItemDiscountMapper orderItemDiscountMapper;
    private final PromotionService promotionService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public OrderItemDiscount create(OrderItemDiscountDto orderItemDiscountDto) {
        OrderItemDiscount discount = orderItemDiscountDto.toOrderItemDiscount();
        stamp(discount, orderItemDiscountDto);
        if (orderItemDiscountMapper.insert(discount) < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return discount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public List<OrderItemDiscount> createBulk(List<OrderItemDiscountDto> orderItemDiscountDtos) {
        if (ObjectUtils.isEmpty(orderItemDiscountDtos)) {
            return List.of();
        }
        List<OrderItemDiscount> discounts = new ArrayList<>(orderItemDiscountDtos.size());
        for (OrderItemDiscountDto dto : orderItemDiscountDtos) {
            OrderItemDiscount discount = dto.toOrderItemDiscount();
            stamp(discount, dto);
            discounts.add(discount);
        }
        orderItemDiscountMapper.insertBatch(discounts);
        return discounts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderItemDiscount> getByOrderItemId(Long orderItemId) {
        return orderItemDiscountMapper.selectByOrderItemId(orderItemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderItemDiscount> getRedemptionSummary(Long promotionId, Date from, Date to) {
        return orderItemDiscountMapper.selectRedemptionSummary(promotionId, from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public int deleteByOrderItemId(Long orderItemId) {
        return orderItemDiscountMapper.deleteByOrderItemId(orderItemId);
    }

    /**
     * Stamps the snapshot timestamp and, when a promotion is referenced, copies its
     * code and name onto the row.
     *
     * @param discount the discount about to be persisted
     * @param dto      the originating DTO, used for the manual discount checks
     */
    private void stamp(OrderItemDiscount discount, OrderItemDiscountDto dto) {
        discount.setCreatedAt(new Date());
        Long promotionId = discount.getPromotionId();
        if (promotionId == null) {
            if (dto.getDiscountType() != DiscountType.MANUAL) {
                throw new BusinessBadRequestException(
                        "exception.orderItemDiscount.badRequest.promotionRequired", new Object[]{dto.getDiscountType()});
            }
            return;
        }
        Promotion promotion = promotionService.get(promotionId);
        discount.setPromotionCode(promotion.getCode())
                .setPromotionName(promotion.getName());
    }
}
