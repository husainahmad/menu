package com.harmoni.pos.business.service.promotion.engine;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionRule;
import com.harmoni.pos.menu.model.PromotionRuleType;
import com.harmoni.pos.menu.model.PromotionScope;
import com.harmoni.pos.menu.model.PromotionScopeType;
import com.harmoni.pos.menu.model.PromotionSchedule;
import com.harmoni.pos.menu.model.PromotionSpecialPrice;
import com.harmoni.pos.menu.model.PromotionTarget;
import com.harmoni.pos.menu.model.PromotionTargetType;
import com.harmoni.pos.menu.model.PromotionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionEngineImplTest {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    /** Monday 5 January 2026, 16:00 UTC. */
    private static final Instant NOW = Instant.parse("2026-01-05T16:00:00Z");
    private static final LocalDate MONDAY = LocalDate.of(2026, 1, 5);

    private static final Long STORE = 100L;
    private static final Long PRODUCT = 10L;
    private static final Long SKU = 20L;
    private static final Long CATEGORY = 30L;

    @Mock
    private PromotionService promotionService;

    private PromotionEngineImpl engine;
    private PromotionContext context;

    @BeforeEach
    void setUp() {
        ORDER_ITEM_SEQUENCE.set(0);
        engine = new PromotionEngineImpl(promotionService, Clock.fixed(NOW, ZONE));
        context = new PromotionContext(STORE, 1L, 2L, 3L, ZONE);
        lenient().when(promotionService.listEvaluableOn(any(LocalDate.class))).thenReturn(List.of());
    }

    private static final AtomicLong ORDER_ITEM_SEQUENCE = new AtomicLong();

    private CartLine line(String unitPrice, String quantity) {
        return new CartLine(ORDER_ITEM_SEQUENCE.incrementAndGet(), PRODUCT, SKU, CATEGORY,
                new BigDecimal(unitPrice), new BigDecimal(quantity));
    }

    private static CartLine lineWithId(long orderItemId, String unitPrice, String quantity) {
        return new CartLine(orderItemId, PRODUCT, SKU, CATEGORY,
                new BigDecimal(unitPrice), new BigDecimal(quantity));
    }

    /**
     * Defaults to a category target covering the line so a test only has to spell out
     * targets when target matching is what it is exercising. The default end date
     * reaches into the following day so the store zone test can cross midnight.
     */
    private static Promotion promotion(Long id, PromotionType type) {
        return new Promotion()
                .setId(id)
                .setCode("CODE" + id)
                .setName("Promo " + id)
                .setPromotionType(type)
                .setPriority(0)
                .setStartDate(MONDAY.minusDays(1))
                .setEndDate(MONDAY.plusDays(1))
                .setTargets(List.of(new PromotionTarget()
                        .setTargetType(PromotionTargetType.CATEGORY)
                        .setCategoryId(CATEGORY)));
    }

    private static PromotionTarget categoryTarget() {
        return new PromotionTarget()
                .setTargetType(PromotionTargetType.CATEGORY)
                .setCategoryId(CATEGORY);
    }

    private static PromotionRule percentage(String value) {
        return new PromotionRule()
                .setRuleType(PromotionRuleType.PERCENTAGE)
                .setDiscountValue(new BigDecimal(value));
    }

    private static PromotionRule fixed(String value) {
        return new PromotionRule()
                .setRuleType(PromotionRuleType.FIXED_AMOUNT)
                .setDiscountValue(new BigDecimal(value));
    }

    private static PromotionTarget skuTarget(Long skuId) {
        return new PromotionTarget().setTargetType(PromotionTargetType.SKU).setSkuId(skuId);
    }

    private void given(Promotion... promotions) {
        when(promotionService.listEvaluableOn(MONDAY)).thenReturn(List.of(promotions));
    }

    @Test
    void percentage_shouldBeComputedOffTheLineAmount() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "3")), context).get(0);

        assertEquals(new BigDecimal("60.00"), outcome.grossAmount());
        assertEquals(new BigDecimal("6.00"), outcome.discountAmount());
        assertEquals(new BigDecimal("54.00"), outcome.netAmount());
        assertEquals(DiscountType.PERCENTAGE, outcome.discounts().get(0).discountType());
        assertEquals("CODE1", outcome.discounts().get(0).promotionCode());
    }

    @Test
    void fixedAmount_shouldBeComputedAndNeverExceedTheLine() {
        given(promotion(1L, PromotionType.FIXED_AMOUNT).setRules(List.of(fixed("5.00"))));
        DiscountOutcome within = engine.evaluate(List.of(line("20.00", "1")), context).get(0);
        assertEquals(new BigDecimal("5.00"), within.discountAmount());

        given(promotion(2L, PromotionType.FIXED_AMOUNT).setRules(List.of(fixed("500.00"))));
        DiscountOutcome beyond = engine.evaluate(List.of(line("20.00", "1")), context).get(0);
        assertEquals(new BigDecimal("20.00"), beyond.discountAmount());
        assertEquals(new BigDecimal("0.00"), beyond.netAmount());
    }

    @Test
    void percentageAboveOneHundred_shouldStillClampToTheLineAmount() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("250"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "2")), context).get(0);

        assertEquals(new BigDecimal("40.00"), outcome.discountAmount());
        assertEquals(new BigDecimal("0.00"), outcome.netAmount());
    }

    @Test
    void multiplePercentageRules_shouldBeSummed() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("10"), percentage("5"))));

        assertEquals(new BigDecimal("9.00"),
                engine.evaluate(List.of(line("60.00", "1")), context).get(0).discountAmount());
    }

    @Test
    void specialPrice_shouldOverrideTheUnitPriceTimesQuantity() {
        Promotion promotion = promotion(1L, PromotionType.SPECIAL_PRICE)
                .setSpecialPrices(List.of(new PromotionSpecialPrice()
                        .setSkuId(SKU)
                        .setSpecialPrice(new BigDecimal("12.50"))));
        given(promotion);

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "4")), context).get(0);

        assertEquals(new BigDecimal("30.00"), outcome.discountAmount());
        assertEquals(DiscountType.SPECIAL_PRICE, outcome.discounts().get(0).discountType());
        assertEquals(new BigDecimal("12.50"), outcome.discounts().get(0).discountValue());
    }

    @Test
    void specialPriceAtOrAboveUnitPrice_shouldGrantNothing() {
        given(promotion(1L, PromotionType.SPECIAL_PRICE)
                .setSpecialPrices(List.of(new PromotionSpecialPrice()
                        .setSkuId(SKU)
                        .setSpecialPrice(new BigDecimal("25.00")))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "2")), context).get(0);

        assertFalse(outcome.isDiscounted());
        assertEquals(new BigDecimal("40.00"), outcome.netAmount());
    }

    @Test
    void promotionStartingInTheFuture_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setStartDate(MONDAY.plusDays(1))
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void promotionThatEnded_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setEndDate(MONDAY.minusDays(1))
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void openEndedPromotion_shouldApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setStartDate(null)
                .setEndDate(null)
                .setRules(List.of(percentage("50"))));

        assertTrue(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void outsideScheduleWindow_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setSchedules(List.of(new PromotionSchedule()
                        .setDayOfWeek(DayOfWeek.MONDAY)
                        .setStartTime(LocalTime.parse("18:00"))
                        .setEndTime(LocalTime.parse("20:00"))
                        .setEnabled(true)))
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void insideScheduleWindow_shouldApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setSchedules(List.of(new PromotionSchedule()
                        .setDayOfWeek(DayOfWeek.MONDAY)
                        .setStartTime(LocalTime.parse("15:00"))
                        .setEndTime(LocalTime.parse("18:00"))
                        .setEnabled(true)))
                .setRules(List.of(percentage("50"))));

        assertTrue(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void scopeForAnotherStore_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setScopes(List.of(new PromotionScope()
                        .setScopeType(PromotionScopeType.STORE).setScopeId(999L)))
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void scopeForThisStore_shouldApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setScopes(List.of(new PromotionScope()
                        .setScopeType(PromotionScopeType.STORE).setScopeId(STORE)))
                .setRules(List.of(percentage("50"))));

        assertTrue(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void noScopes_shouldBeTreatedAsGlobal() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("50"))));

        assertTrue(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void storeScopedPromotion_shouldNotApply_whenContextHasNoStore() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setScopes(List.of(new PromotionScope()
                        .setScopeType(PromotionScopeType.STORE).setScopeId(STORE)))
                .setRules(List.of(percentage("50"))));

        PromotionContext noStore = new PromotionContext(null, 1L, 2L, 3L, ZONE);

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), noStore).get(0).isDiscounted());
    }

    @Test
    void categoryTarget_shouldMatchAndReportLowestSpecificity() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "1")), context).get(0);

        assertEquals(TargetMatch.CATEGORY, outcome.discounts().get(0).targetMatch());
    }

    @Test
    void targetForAnotherSku_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(skuTarget(999L)))
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void promotionWithNoTargets_shouldNotApply() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of())
                .setRules(List.of(percentage("50"))));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void mostSpecificTargetShouldBeReported() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(
                        categoryTarget(),
                        new PromotionTarget().setTargetType(PromotionTargetType.PRODUCT).setProductId(PRODUCT),
                        skuTarget(SKU)))
                .setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "1")), context).get(0);

        assertEquals(TargetMatch.SKU, outcome.discounts().get(0).targetMatch());
    }

    @Test
    void minQuantityGuard_shouldBlockBelowTheThreshold() {
        PromotionRule guard = new PromotionRule()
                .setRuleType(PromotionRuleType.MIN_QUANTITY)
                .setMinQuantity(new BigDecimal("3"));
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("10"), guard)));

        assertFalse(engine.evaluate(List.of(line("20.00", "2")), context).get(0).isDiscounted());
        assertTrue(engine.evaluate(List.of(line("20.00", "3")), context).get(0).isDiscounted());
    }

    @Test
    void minAmountGuard_shouldBlockBelowTheThreshold() {
        PromotionRule guard = new PromotionRule()
                .setRuleType(PromotionRuleType.MIN_AMOUNT)
                .setMinAmount(new BigDecimal("50.00"));
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of(percentage("10"), guard)));

        assertFalse(engine.evaluate(List.of(line("20.00", "2")), context).get(0).isDiscounted());
        assertTrue(engine.evaluate(List.of(line("20.00", "3")), context).get(0).isDiscounted());
    }

    @Test
    void exclusivePolicy_shouldGrantOnlyTheLargerDiscount() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("10"))),
                promotion(2L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("40"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("50.00", "1")), context).get(0);

        assertEquals(1, outcome.discounts().size());
        assertEquals(2L, outcome.discounts().get(0).promotionId());
        assertEquals(new BigDecimal("20.00"), outcome.discountAmount());
    }

    @Test
    void stackedPolicy_shouldSumEveryEligibleDiscount() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("10"))),
                promotion(2L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("40"))));

        DiscountOutcome outcome = engine.evaluateStacked(List.of(line("50.00", "1")), context).get(0);

        assertEquals(2, outcome.discounts().size());
        assertEquals(new BigDecimal("25.00"), outcome.discountAmount());
    }

    @Test
    void exclusiveTieOnAmount_shouldPreferTheMoreSpecificTarget() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(categoryTarget()))
                        .setRules(List.of(percentage("10"))),
                promotion(2L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("50.00", "1")), context).get(0);

        assertEquals(1, outcome.discounts().size());
        assertEquals(2L, outcome.discounts().get(0).promotionId());
    }

    @Test
    void maxDiscountAmount_shouldCapThePromotionProportionallyAcrossTheOrder() {
        PromotionRule cap = new PromotionRule()
                .setRuleType(PromotionRuleType.MAX_DISCOUNT_AMOUNT)
                .setMaxDiscountAmount(new BigDecimal("15.00"));
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("50"), cap)));

        List<DiscountOutcome> outcomes = engine.evaluate(
                List.of(line("20.00", "1"), line("20.00", "1"), line("20.00", "1")), context);

        assertEquals(new BigDecimal("5.00"), outcomes.get(0).discountAmount());
        assertEquals(new BigDecimal("5.00"), outcomes.get(1).discountAmount());
        assertEquals(new BigDecimal("5.00"), outcomes.get(2).discountAmount());
        assertEquals(new BigDecimal("15.00"), outcomes.stream()
                .map(DiscountOutcome::discountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void maxDiscountAmount_shouldScaleUnevenLinesProportionallyNotGreedily() {
        PromotionRule cap = new PromotionRule()
                .setRuleType(PromotionRuleType.MAX_DISCOUNT_AMOUNT)
                .setMaxDiscountAmount(new BigDecimal("12.00"));
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("50"), cap)));

        List<DiscountOutcome> outcomes = engine.evaluate(
                List.of(line("100.00", "1"), line("20.00", "1")), context);

        assertEquals(new BigDecimal("10.00"), outcomes.get(0).discountAmount());
        assertEquals(new BigDecimal("2.00"), outcomes.get(1).discountAmount());
    }

    @Test
    void maxDiscountAmountAboveWhatIsGranted_shouldNotDisturb() {
        PromotionRule cap = new PromotionRule()
                .setRuleType(PromotionRuleType.MAX_DISCOUNT_AMOUNT)
                .setMaxDiscountAmount(new BigDecimal("1000.00"));
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setRules(List.of(percentage("10"), cap)));

        assertEquals(new BigDecimal("6.00"),
                engine.evaluate(List.of(line("20.00", "3")), context).get(0).discountAmount());
    }

    @Test
    void exclusivePolicy_shouldHonourACapEvenWhenItLosesToAnEqualUncappedPromotion() {
        PromotionRule cap = new PromotionRule()
                .setRuleType(PromotionRuleType.MAX_DISCOUNT_AMOUNT)
                .setMaxDiscountAmount(new BigDecimal("1.00"));
        given(promotion(1L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(skuTarget(SKU)))
                        .setRules(List.of(percentage("10"), cap)),
                promotion(2L, PromotionType.PERCENTAGE)
                        .setTargets(List.of(categoryTarget()))
                        .setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "1")), context).get(0);

        assertEquals(1, outcome.discounts().size());
        assertEquals(1L, outcome.discounts().get(0).promotionId());
        assertEquals(new BigDecimal("1.00"), outcome.discountAmount());
    }

    @Test
    void zeroPricedLine_shouldNeverBeDiscounted() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("100"))));

        DiscountOutcome outcome = engine.evaluate(List.of(line("0.00", "5")), context).get(0);

        assertFalse(outcome.isDiscounted());
        assertEquals(new BigDecimal("0.00"), outcome.netAmount());
    }

    @Test
    void emptyBasket_shouldReturnNoOutcomes() {
        assertTrue(engine.evaluate(List.of(), context).isEmpty());
        assertTrue(engine.evaluate(null, context).isEmpty());
    }

    @Test
    void noCandidatePromotions_shouldLeaveEveryLineAtFullPrice() {
        DiscountOutcome outcome = engine.evaluate(List.of(line("20.00", "2")), context).get(0);

        assertEquals(new BigDecimal("40.00"), outcome.grossAmount());
        assertEquals(new BigDecimal("40.00"), outcome.netAmount());
        assertFalse(outcome.isDiscounted());
    }

    @Test
    void liveButUnpriceablePromotionType_shouldFailLoudly() {
        given(promotion(1L, PromotionType.BUNDLE));

        BusinessBadRequestException thrown = assertThrows(BusinessBadRequestException.class,
                () -> engine.evaluate(List.of(line("20.00", "1")), context));
        assertEquals("exception.promotion.engine.unsupportedType", thrown.getMessage());
        assertArrayEquals(new Object[]{1L, PromotionType.BUNDLE}, thrown.getArgs());
    }

    @Test
    void percentagePromotionWithoutAPercentageRule_shouldGrantNothing() {
        given(promotion(1L, PromotionType.PERCENTAGE).setRules(List.of()));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), context).get(0).isDiscounted());
    }

    @Test
    void computeDiscounts_shouldFlattenAcrossLines() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("10"))));

        List<AppliedDiscount> discounts = engine
                .computeDiscounts(List.of(line("20.00", "1"), line("30.00", "1")), context);

        assertEquals(2, discounts.size());
        assertEquals(new BigDecimal("2.00"), discounts.get(0).discountAmount());
        assertEquals(new BigDecimal("3.00"), discounts.get(1).discountAmount());
        assertEquals("CODE1", discounts.get(0).promotionCode());
        assertEquals("Promo 1", discounts.get(0).promotionName());
    }

    @Test
    void outcomeList_shouldMatchTheInputLinesInOrder() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("10"))));

        List<CartLine> lines = List.of(
                new CartLine(11L, PRODUCT, SKU, CATEGORY, new BigDecimal("10.00"), new BigDecimal("1")),
                new CartLine(22L, PRODUCT, SKU, CATEGORY, new BigDecimal("20.00"), new BigDecimal("1")),
                new CartLine(33L, PRODUCT, SKU, CATEGORY, new BigDecimal("30.00"), new BigDecimal("1")));

        List<DiscountOutcome> outcomes = engine.evaluate(lines, context);

        assertEquals(3, outcomes.size());
        assertEquals(11L, outcomes.get(0).orderItemId());
        assertEquals(22L, outcomes.get(1).orderItemId());
        assertEquals(33L, outcomes.get(2).orderItemId());
    }

    @Test
    void fractionalQuantity_shouldBePricedWithoutFloatingPointDrift() {
        given(promotion(1L, PromotionType.PERCENTAGE)
                .setTargets(List.of(categoryTarget()))
                .setRules(List.of(percentage("10"))));

        DiscountOutcome outcome = engine
                .evaluate(List.of(new CartLine(1L, PRODUCT, SKU, CATEGORY,
                        new BigDecimal("3.33"), new BigDecimal("3"))), context)
                .get(0);

        assertEquals(new BigDecimal("9.99"), outcome.grossAmount());
        assertEquals(new BigDecimal("1.00"), outcome.discountAmount());
    }

    /**
     * 16:00 UTC on Monday is 03:00 on Tuesday in Sydney, so a promotion open all of
     * Tuesday is live for the store and not for the same instant read as UTC. The
     * promotion's date range reaches into Tuesday, which is what makes this a genuine
     * zone test rather than a date range accident.
     */
    @Test
    void evaluation_shouldHonourTheStoreZoneRatherThanTheServerDefault() {
        Promotion promotion = promotion(1L, PromotionType.PERCENTAGE)
                .setSchedules(List.of(new PromotionSchedule()
                        .setDayOfWeek(DayOfWeek.TUESDAY)
                        .setStartTime(LocalTime.parse("00:00"))
                        .setEndTime(LocalTime.parse("23:59"))
                        .setEnabled(true)))
                .setTargets(List.of(skuTarget(SKU)))
                .setRules(List.of(percentage("10")));
        // The same instant is Monday in UTC and Tuesday in Sydney, so the candidate set
        // has to be offered for both dates or the second lookup falls through to empty.
        when(promotionService.listEvaluableOn(any(LocalDate.class))).thenReturn(List.of(promotion));

        PromotionContext utc = new PromotionContext(STORE, null, null, null, ZoneOffset.UTC);
        PromotionContext ahead = new PromotionContext(STORE, null, null, null, ZoneId.of("Australia/Sydney"));

        assertFalse(engine.evaluate(List.of(line("20.00", "1")), utc).get(0).isDiscounted());
        assertTrue(engine.evaluate(List.of(line("20.00", "1")), ahead).get(0).isDiscounted());
    }
}
