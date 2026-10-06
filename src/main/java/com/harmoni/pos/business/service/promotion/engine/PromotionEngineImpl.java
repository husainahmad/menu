package com.harmoni.pos.business.service.promotion.engine;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionRule;
import com.harmoni.pos.menu.model.PromotionRuleType;
import com.harmoni.pos.menu.model.PromotionScope;
import com.harmoni.pos.menu.model.PromotionScopeType;
import com.harmoni.pos.menu.model.PromotionSpecialPrice;
import com.harmoni.pos.menu.model.PromotionTarget;
import com.harmoni.pos.menu.model.PromotionTargetType;
import com.harmoni.pos.menu.model.PromotionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link PromotionEngine}.
 * <p>
 * Reads candidates through {@link PromotionService#listEvaluableOn(LocalDate)} rather
 * than the mappers, so the engine sits above the persistence layer and the promotion
 * rules can be exercised against a mocked service with no datasource.
 * <p>
 * Evaluation runs in three stages:
 * <ol>
 *   <li><b>Eligibility</b>, once per promotion rather than once per line: status,
 *       date range, schedule window and scope. Anything that fails is dropped before
 *       any line is looked at.</li>
 *   <li><b>Per line</b>, for each surviving promotion: target match, then the
 *       {@code MIN_*} guard rules, then the discount itself.</li>
 *   <li><b>Resolution</b>: the {@link StackingPolicy} decides which of the competing
 *       per line candidates survive, then every promotion's
 *       {@code MAX_DISCOUNT_AMOUNT} cap is enforced across the whole order, then each
 *       line is clamped so a discount can never exceed what the line is worth.</li>
 * </ol>
 * The clamp runs last on purpose. It is the single invariant that makes the engine safe
 * to expose to a client that would rather not be trusted.
 *
 * @author husainahmad
 */
@Service("promotionEngine")
@RequiredArgsConstructor
@Slf4j
public class PromotionEngineImpl implements PromotionEngine {

    private static final int MONEY_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * Percentage rules on one promotion are additive. Summing is predictable and the
     * per line clamp downstream stops a misconfigured 200 percent promotion from
     * making a line free.
     */
    private static final StackingPolicy DEFAULT_POLICY = StackingPolicy.EXCLUSIVE;

    private final PromotionService promotionService;
    private final Clock clock;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscountOutcome> evaluate(List<CartLine> lines, PromotionContext context) {
        return evaluate(lines, context, DEFAULT_POLICY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscountOutcome> evaluateExclusive(List<CartLine> lines, PromotionContext context) {
        return evaluate(lines, context, StackingPolicy.EXCLUSIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscountOutcome> evaluateStacked(List<CartLine> lines, PromotionContext context) {
        return evaluate(lines, context, StackingPolicy.STACKED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppliedDiscount> computeDiscounts(List<CartLine> lines, PromotionContext context) {
        return evaluate(lines, context, DEFAULT_POLICY).stream()
                .flatMap(outcome -> outcome.discounts().stream())
                .toList();
    }

    /**
     * Prices a cart under an explicit stacking policy.
     *
     * @param lines   the cart lines
     * @param context the store, chain and zone the sale is happening in
     * @param policy  how competing promotions on one line are resolved
     * @return one outcome per input line, in the same order
     */
    private List<DiscountOutcome> evaluate(List<CartLine> lines,
                                           PromotionContext context,
                                           StackingPolicy policy) {
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }
        PromotionContext effectiveContext = context == null ? new PromotionContext(null, null, null, null, null) : context;
        ZonedDateTime now = ZonedDateTime.now(clock.withZone(effectiveContext.zone()));
        List<Promotion> eligible = eligiblePromotions(now, effectiveContext);

        List<List<AppliedDiscount>> perLine = new ArrayList<>(lines.size());
        for (CartLine line : lines) {
            List<AppliedDiscount> resolved = resolveLine(line, eligible, policy);
            clampToLineAmount(resolved, line);
            perLine.add(resolved);
        }
        enforceOrderLevelCaps(perLine, eligible);

        List<DiscountOutcome> outcomes = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            CartLine line = lines.get(i);
            outcomes.add(new DiscountOutcome(line.orderItemId(), line.grossAmount(), perLine.get(i)));
        }
        return outcomes;
    }

    /**
     * Stage one. Filters the candidate set down to what may be priced right now.
     */
    private List<Promotion> eligiblePromotions(ZonedDateTime now, PromotionContext context) {
        List<Promotion> candidates = promotionService.listEvaluableOn(now.toLocalDate());
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        LocalDate today = now.toLocalDate();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalTime timeOfDay = now.toLocalTime();

        List<Promotion> eligible = new ArrayList<>(candidates.size());
        for (Promotion promotion : candidates) {
            if (!coversDateRange(promotion, today)) {
                continue;
            }
            if (!PromotionScheduleMatcher.isRedeemable(promotion.getSchedules(), dayOfWeek, timeOfDay)) {
                log.debug("Promotion {} skipped, outside its schedule window", promotion.getId());
                continue;
            }
            if (!isInScope(promotion.getScopes(), context)) {
                log.debug("Promotion {} skipped, out of scope for store {}", promotion.getId(), context.storeId());
                continue;
            }
            if (promotion.getPromotionType() == PromotionType.BUY_X_GET_Y
                    || promotion.getPromotionType() == PromotionType.BUNDLE) {
                throw new BusinessBadRequestException("exception.promotion.engine.unsupportedType",
                        new Object[]{promotion.getId(), promotion.getPromotionType()});
            }
            eligible.add(promotion);
        }
        eligible.sort(Comparator.comparing(Promotion::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Promotion::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        return eligible;
    }

    private static boolean coversDateRange(Promotion promotion, LocalDate today) {
        LocalDate start = promotion.getStartDate();
        LocalDate end = promotion.getEndDate();
        if (start != null && today.isBefore(start)) {
            return false;
        }
        return end == null || !today.isAfter(end);
    }

    /**
     * A promotion with no scope rows is global and applies everywhere. That is the
     * intuitive authoring default, and the alternative, treating an unscoped promotion
     * as matching nothing, would silently disable every promotion created without
     * thinking about tenancy.
     * <p>
     * Otherwise the scopes are OR-ed: a promotion scoped to both a tenant and one of its
     * stores applies when either matches. A null reference in the context can never
     * match, so a basket evaluated without a store never picks up a store promotion.
     */
    private static boolean isInScope(List<PromotionScope> scopes, PromotionContext context) {
        if (scopes == null || scopes.isEmpty()) {
            return true;
        }
        for (PromotionScope scope : scopes) {
            if (matchesScope(scope, context)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesScope(PromotionScope scope, PromotionContext context) {
        PromotionScopeType type = scope.getScopeType();
        Long scopeId = scope.getScopeId();
        if (type == null || scopeId == null) {
            return false;
        }
        return switch (type) {
            case TENANT -> scopeId.equals(context.tenantId());
            case BRAND -> scopeId.equals(context.brandId());
            case CHAIN -> scopeId.equals(context.chainId());
            case STORE -> scopeId.equals(context.storeId());
        };
    }

    /**
     * Stage two and the start of stage three: every promotion that wants this line,
     * narrowed to the ones the policy allows.
     */
    private List<AppliedDiscount> resolveLine(CartLine line, List<Promotion> eligible, StackingPolicy policy) {
        if (line == null || !line.isDiscountable()) {
            return List.of();
        }
        BigDecimal gross = line.grossAmount();
        List<AppliedDiscount> candidates = new ArrayList<>();
        for (Promotion promotion : eligible) {
            TargetMatch match = matchTargets(promotion.getTargets(), line);
            if (!match.isMatch()) {
                continue;
            }
            if (!guardsSatisfied(promotion.getRules(), line, gross)) {
                continue;
            }
            AppliedDiscount discount = computeDiscount(promotion, line, gross, match);
            if (discount != null && discount.discountAmount().signum() > 0) {
                candidates.add(discount);
            }
        }
        if (candidates.isEmpty()) {
            return new ArrayList<>();
        }
        // Always mutable: enforceOrderLevelCaps rewrites amounts in place once an
        // order level cap bites.
        return policy == StackingPolicy.STACKED ? candidates : new ArrayList<>(List.of(best(candidates)));
    }

    /**
     * Picks the single discount to grant when the policy is
     * {@link StackingPolicy#EXCLUSIVE}.
     * <p>
     * The largest discount wins, because a customer who has two live promotions
     * qualifying for a line should be charged the better of the two rather than
     * whichever happened to be configured with the lower priority number. Priority is
     * the tie-break, not the primary rule: it only decides between two promotions that
     * would grant exactly the same amount, and {@code eligible} is already sorted by it,
     * so keeping the incumbent on a tie is enough and the lowest promotion id settles
     * anything the ordering left level.
     */
    private static AppliedDiscount best(List<AppliedDiscount> candidates) {
        AppliedDiscount winner = null;
        for (AppliedDiscount candidate : candidates) {
            if (winner == null
                    || candidate.discountAmount().compareTo(winner.discountAmount()) > 0
                    || (candidate.discountAmount().compareTo(winner.discountAmount()) == 0
                    && candidate.targetMatch().rank() > winner.targetMatch().rank())
                    || (candidate.discountAmount().compareTo(winner.discountAmount()) == 0
                    && candidate.targetMatch().rank() == winner.targetMatch().rank()
                    && lowerPromotionId(candidate, winner))) {
                winner = candidate;
            }
        }
        return winner;
    }

    private static boolean lowerPromotionId(AppliedDiscount candidate, AppliedDiscount winner) {
        if (candidate.promotionId() == null) {
            return false;
        }
        return winner.promotionId() != null && candidate.promotionId() < winner.promotionId();
    }

    /**
     * The most specific target level on the promotion that covers the line, following
     * the SKU over product over category precedence documented on
     * {@link PromotionTarget}.
     */
    private static TargetMatch matchTargets(List<PromotionTarget> targets, CartLine line) {
        if (targets == null || targets.isEmpty()) {
            return TargetMatch.NONE;
        }
        TargetMatch best = TargetMatch.NONE;
        for (PromotionTarget target : targets) {
            PromotionTargetType type = target.getTargetType();
            if (type == null) {
                continue;
            }
            boolean hit = switch (type) {
                case SKU -> line.skuId() != null && line.skuId().equals(target.getSkuId());
                case PRODUCT -> line.productId() != null && line.productId().equals(target.getProductId());
                case CATEGORY -> line.categoryId() != null && line.categoryId().equals(target.getCategoryId());
            };
            if (!hit) {
                continue;
            }
            TargetMatch candidate = switch (type) {
                case SKU -> TargetMatch.SKU;
                case PRODUCT -> TargetMatch.PRODUCT;
                case CATEGORY -> TargetMatch.CATEGORY;
            };
            best = best.mostSpecific(candidate);
        }
        return best;
    }

    /**
     * The {@code MIN_*} rules are guards: every one of them has to hold or the promotion
     * does not apply to the line. {@code MAX_DISCOUNT_AMOUNT} is not checked here
     * because it caps the whole order, not the line.
     */
    private static boolean guardsSatisfied(List<PromotionRule> rules, CartLine line, BigDecimal gross) {
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        for (PromotionRule rule : rules) {
            PromotionRuleType type = rule.getRuleType();
            if (type == null) {
                continue;
            }
            boolean satisfied = switch (type) {
                case MIN_QUANTITY -> atLeast(rule.getMinQuantity(), line.quantity());
                case MIN_AMOUNT -> atLeast(rule.getMinAmount(), gross);
                case PERCENTAGE, FIXED_AMOUNT, MAX_DISCOUNT_AMOUNT -> true;
            };
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }

    private static boolean atLeast(BigDecimal threshold, BigDecimal actual) {
        return threshold == null || actual.compareTo(threshold) >= 0;
    }

    /**
     * Turns a matched promotion into a money amount, or null when the promotion is
     * configured in a way that grants nothing.
     */
    private static AppliedDiscount computeDiscount(Promotion promotion,
                                                   CartLine line,
                                                   BigDecimal gross,
                                                   TargetMatch match) {
        return switch (promotion.getPromotionType()) {
            case PERCENTAGE -> magnitudeDiscount(promotion, line, gross, match,
                    PromotionRuleType.PERCENTAGE, DiscountType.PERCENTAGE, true);
            case FIXED_AMOUNT -> magnitudeDiscount(promotion, line, gross, match,
                    PromotionRuleType.FIXED_AMOUNT, DiscountType.FIXED_AMOUNT, false);
            case SPECIAL_PRICE -> specialPriceDiscount(promotion, line, match);
            case BUY_X_GET_Y, BUNDLE -> throw new IllegalStateException(
                    "unsupported promotion type reached pricing, eligibility filter should have rejected "
                            + promotion.getPromotionType());
        };
    }

    private static AppliedDiscount magnitudeDiscount(Promotion promotion,
                                                     CartLine line,
                                                     BigDecimal gross,
                                                     TargetMatch match,
                                                     PromotionRuleType ruleType,
                                                     DiscountType discountType,
                                                     boolean percentage) {
        BigDecimal value = sumRuleValues(promotion.getRules(), ruleType);
        if (value == null || value.signum() <= 0) {
            log.warn("Promotion {} is a {} promotion with no usable {} rule, skipping",
                    promotion.getId(), promotion.getPromotionType(), ruleType);
            return null;
        }
        BigDecimal amount = percentage
                ? gross.multiply(value).divide(ONE_HUNDRED, MONEY_SCALE, RoundingMode.HALF_UP)
                : value.min(gross);
        return appliedDiscount(promotion, line, discountType, value, amount, match);
    }

    /**
     * A special price is a per unit override, so the discount is the gap between the
     * price being charged and the promotional one, times the quantity. A special price
     * at or above the tier price grants nothing rather than a negative discount.
     */
    private static AppliedDiscount specialPriceDiscount(Promotion promotion, CartLine line, TargetMatch match) {
        Long skuId = line.skuId();
        if (skuId == null) {
            return null;
        }
        for (PromotionSpecialPrice specialPrice : orEmpty(promotion.getSpecialPrices())) {
            if (!skuId.equals(specialPrice.getSkuId()) || specialPrice.getSpecialPrice() == null) {
                continue;
            }
            BigDecimal override = specialPrice.getSpecialPrice().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal amount = line.unitPrice().subtract(override).max(BigDecimal.ZERO)
                    .multiply(line.quantity())
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            return appliedDiscount(promotion, line, DiscountType.SPECIAL_PRICE, override, amount, match);
        }
        return null;
    }

    private static AppliedDiscount appliedDiscount(Promotion promotion,
                                                   CartLine line,
                                                   DiscountType discountType,
                                                   BigDecimal discountValue,
                                                   BigDecimal amount,
                                                   TargetMatch match) {
        return new AppliedDiscount(line.orderItemId(), promotion.getId(), promotion.getCode(),
                promotion.getName(), discountType, discountValue, amount, match);
    }

    private static BigDecimal sumRuleValues(List<PromotionRule> rules, PromotionRuleType ruleType) {
        if (rules == null) {
            return null;
        }
        BigDecimal total = null;
        for (PromotionRule rule : rules) {
            if (rule.getRuleType() != ruleType || rule.getDiscountValue() == null) {
                continue;
            }
            total = total == null ? rule.getDiscountValue() : total.add(rule.getDiscountValue());
        }
        return total;
    }

    /**
     * The invariant that makes the engine safe to expose to a client: no line is ever
     * discounted by more than it is worth, so a net amount can never go negative.
     * <p>
     * A misconfigured percentage above one hundred, or a stacked basket of aggressive
     * promotions, can both produce a nominal total larger than the line. When that
     * happens the amounts are scaled down in proportion and the rounding residue is
     * handed back out, so the discount is reduced rather than silently truncated and the
     * recorded total still adds up to the line.
     */
    private static void clampToLineAmount(List<AppliedDiscount> discounts, CartLine line) {
        if (discounts.isEmpty() || !line.isDiscountable()) {
            return;
        }
        BigDecimal gross = line.grossAmount();
        BigDecimal total = totalOf(discounts);
        if (total.compareTo(gross) <= 0) {
            return;
        }
        log.warn("Discounts totalling {} exceed the line amount {} for order item {}, scaling down",
                total, gross, line.orderItemId());
        scaleInPlace(discounts, gross);
    }

    /**
     * Rewrites every amount in a list so the list sums to exactly {@code target},
     * proportionally where possible and by handing the rounding residue to the largest
     * entries.
     * <p>
     * Proportional rather than first entry first, because greedy allocation would let a
     * cheap line eat a cap and silently reduce the discount on an expensive line
     * further down, which reads to a customer as the promotion changing as they shop.
     */
    private static void scaleInPlace(List<AppliedDiscount> discounts, BigDecimal target) {
        BigDecimal total = totalOf(discounts);
        if (total.signum() <= 0) {
            return;
        }
        for (int i = 0; i < discounts.size(); i++) {
            BigDecimal scaled = discounts.get(i).discountAmount()
                    .multiply(target)
                    .divide(total, MONEY_SCALE, RoundingMode.DOWN);
            discounts.set(i, withAmount(discounts.get(i), scaled));
        }
        handOutRemainder(discounts, target);
    }

    /**
     * Adds back the pennies lost to rounding down, largest entry first, so the list sums
     * to exactly {@code target} without any entry exceeding what it was scaled to.
     */
    private static void handOutRemainder(List<AppliedDiscount> discounts, BigDecimal target) {
        BigDecimal residue = target.subtract(totalOf(discounts));
        if (residue.signum() <= 0) {
            return;
        }
        BigDecimal penny = new BigDecimal("0.01");
        List<Integer> bySizeDesc = new ArrayList<>();
        for (int i = 0; i < discounts.size(); i++) {
            bySizeDesc.add(i);
        }
        bySizeDesc.sort(Comparator.comparing((Integer i) -> discounts.get(i).discountAmount()).reversed());
        int pennies = residue.divide(penny, 0, RoundingMode.DOWN).intValueExact();
        for (int i = 0; i < pennies && i < bySizeDesc.size(); i++) {
            int index = bySizeDesc.get(i);
            discounts.set(index, withAmount(discounts.get(index),
                    discounts.get(index).discountAmount().add(penny)));
        }
    }

    private static BigDecimal totalOf(List<AppliedDiscount> discounts) {
        BigDecimal total = BigDecimal.ZERO;
        for (AppliedDiscount discount : discounts) {
            total = total.add(discount.discountAmount());
        }
        return total;
    }

    /**
     * Stage three, order level. A {@code MAX_DISCOUNT_AMOUNT} rule caps what one
     * promotion may give away across the entire basket, so when the cap bites the
     * amounts that promotion granted are scaled down and the total is brought to exactly
     * the cap.
     */
    private static void enforceOrderLevelCaps(List<List<AppliedDiscount>> perLine, List<Promotion> eligible) {
        for (Promotion promotion : eligible) {
            BigDecimal cap = capOf(promotion);
            if (cap != null) {
                scalePromotionToCap(perLine, promotion.getId(), cap);
            }
        }
    }

    private static BigDecimal capOf(Promotion promotion) {
        if (promotion.getRules() == null) {
            return null;
        }
        for (PromotionRule rule : promotion.getRules()) {
            if (rule.getRuleType() == PromotionRuleType.MAX_DISCOUNT_AMOUNT && rule.getMaxDiscountAmount() != null) {
                return rule.getMaxDiscountAmount();
            }
        }
        return null;
    }

    /**
     * Gathers every discount one promotion granted across the basket and scales them to
     * the cap if they overshoot.
     * <p>
     * Works through the exact list instances the caller holds rather than searching by
     * order item ID. A basket that reused an ID would otherwise make an adjustment land
     * on the wrong line.
     */
    private static void scalePromotionToCap(List<List<AppliedDiscount>> perLine, Long promotionId, BigDecimal cap) {
        List<List<AppliedDiscount>> holders = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        List<AppliedDiscount> granted = new ArrayList<>();
        for (List<AppliedDiscount> discounts : perLine) {
            for (int i = 0; i < discounts.size(); i++) {
                if (Objects.equals(discounts.get(i).promotionId(), promotionId)) {
                    holders.add(discounts);
                    indexes.add(i);
                    granted.add(discounts.get(i));
                }
            }
        }
        if (granted.isEmpty()) {
            return;
        }
        BigDecimal total = totalOf(granted);
        if (total.compareTo(cap) <= 0) {
            return;
        }
        log.debug("Promotion {} granted {} against a cap of {}, scaling down", promotionId, total, cap);
        for (int i = 0; i < granted.size(); i++) {
            BigDecimal scaled = granted.get(i).discountAmount()
                    .multiply(cap)
                    .divide(total, MONEY_SCALE, RoundingMode.DOWN);
            setAmount(holders.get(i), indexes.get(i), scaled);
        }
        handOutRemainderAcross(holders, indexes, cap);
    }

    /**
     * The cross line counterpart of {@link #handOutRemainder(List, BigDecimal)}: the
     * capped amounts are spread over several lists, so the residue is handed out by
     * walking the flattened view while writing back through the held references.
     */
    private static void handOutRemainderAcross(List<List<AppliedDiscount>> holders,
                                               List<Integer> indexes,
                                               BigDecimal target) {
        BigDecimal distributed = BigDecimal.ZERO;
        for (int i = 0; i < holders.size(); i++) {
            distributed = distributed.add(holders.get(i).get(indexes.get(i)).discountAmount());
        }
        BigDecimal residue = target.subtract(distributed);
        if (residue.signum() <= 0) {
            return;
        }
        BigDecimal penny = new BigDecimal("0.01");
        int pennies = residue.divide(penny, 0, RoundingMode.DOWN).intValueExact();
        Integer[] order = new Integer[holders.size()];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        Arrays.sort(order, Comparator
                .comparing((Integer i) -> holders.get(i).get(indexes.get(i)).discountAmount())
                .reversed());
        for (int i = 0; i < pennies && i < order.length; i++) {
            int slot = order[i];
            BigDecimal current = holders.get(slot).get(indexes.get(slot)).discountAmount();
            setAmount(holders.get(slot), indexes.get(slot), current.add(penny));
        }
    }

    private static void setAmount(List<AppliedDiscount> discounts, int index, BigDecimal amount) {
        discounts.set(index, withAmount(discounts.get(index), amount));
    }

    private static AppliedDiscount withAmount(AppliedDiscount discount, BigDecimal amount) {
        return new AppliedDiscount(discount.orderItemId(), discount.promotionId(), discount.promotionCode(),
                discount.promotionName(), discount.discountType(), discount.discountValue(), amount,
                discount.targetMatch());
    }

    private static <T> List<T> orEmpty(List<T> rows) {
        return rows == null ? List.of() : rows;
    }
}
