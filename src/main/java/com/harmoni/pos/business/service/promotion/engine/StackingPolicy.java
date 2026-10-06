package com.harmoni.pos.business.service.promotion.engine;

/**
 * How the engine resolves several eligible promotions competing for the same cart
 * line.
 * <p>
 * {@link com.harmoni.pos.menu.model.Promotion#priority} orders the candidates but
 * does not say what happens when more than one survives. Without an explicit policy
 * that is a silent revenue decision, so it is a parameter of the engine rather than a
 * hidden default.
 *
 * @author husainahmad
 */
public enum StackingPolicy {

    /**
     * The single largest discount wins and the rest are discarded. This is the safe
     * default: the customer can never combine two promotions to exceed what either
     * one promised, and a
     * {@link com.harmoni.pos.menu.model.PromotionRuleType#MAX_DISCOUNT_AMOUNT} cap
     * is trivially satisfied.
     */
    EXCLUSIVE,

    /**
     * Every eligible discount is summed. Ties are broken by promotion priority and
     * then by target specificity, so the outcome stays deterministic. A
     * {@link com.harmoni.pos.menu.model.PromotionRuleType#MAX_DISCOUNT_AMOUNT} cap
     * is enforced across the whole order and the granted amounts are scaled down
     * proportionally when the cap bites.
     * <p>
     * Stacking turns every guard rule into a liability: a percentage promotion with
     * no {@code MAX_DISCOUNT_AMOUNT} can compound with any other active promotion.
     * Only enable it once the promotion catalogue is curated.
     */
    STACKED
}
