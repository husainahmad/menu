package com.harmoni.pos.business.service.promotion.pricing;

import com.harmoni.pos.business.service.promotion.engine.CartLine;
import com.harmoni.pos.business.service.promotion.engine.DiscountOutcome;
import com.harmoni.pos.business.service.promotion.engine.PromotionContext;
import com.harmoni.pos.business.service.promotion.engine.PromotionEngine;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.dto.pricing.CartLineDto;
import com.harmoni.pos.menu.model.dto.pricing.PricedLineDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Translates a priced-basket request into engine types and the engine's answer back
 * into a priced-basket response.
 *
 * @author husainahmad
 */
@RequiredArgsConstructor
@Service("promotionPricingService")
@Slf4j
public class PromotionPricingServiceImpl implements PromotionPricingService {

    private final PromotionEngine promotionEngine;

    /**
     * {@inheritDoc}
     *
     * <p>The response is assembled by position rather than by looking the outcomes up,
     * so a caller always gets one entry per requested line in the order it asked for,
     * even if the engine were ever to change the order it returns them in.</p>
     */
    @Override
    public PromotionPriceResponseDto price(PromotionPriceRequestDto request) {
        if (request == null || ObjectUtils.isEmpty(request.getLines())) {
            throw new BusinessBadRequestException("exception.promotionPrice.emptyRequest", null);
        }
        PromotionContext context = toContext(request);
        List<CartLineDto> requested = request.getLines();

        List<CartLine> lines = new ArrayList<>(requested.size());
        for (CartLineDto line : requested) {
            lines.add(line.toCartLine());
        }

        List<DiscountOutcome> outcomes = promotionEngine.evaluate(lines, context);

        List<PricedLineDto> priced = new ArrayList<>(requested.size());
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        for (int i = 0; i < requested.size(); i++) {
            PricedLineDto pricedLine = PricedLineDto.of(i, outcomes.get(i));
            priced.add(pricedLine);
            totalDiscount = totalDiscount.add(pricedLine.getDiscountAmount());
            totalNet = totalNet.add(pricedLine.getNetAmount());
        }
        log.debug("Priced {} line(s), discounting {}", priced.size(), totalDiscount);

        PromotionPriceResponseDto response = new PromotionPriceResponseDto();
        response.setLines(priced);
        response.setTotalDiscount(totalDiscount);
        response.setTotalNet(totalNet);
        return response;
    }

    private static PromotionContext toContext(PromotionPriceRequestDto request) {
        if (request.getContext() == null) {
            throw new BusinessBadRequestException("exception.promotionPrice.contextRequired", null);
        }
        return request.getContext().toPromotionContext();
    }
}
