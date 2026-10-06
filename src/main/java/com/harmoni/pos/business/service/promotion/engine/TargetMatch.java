package com.harmoni.pos.business.service.promotion.engine;

/**
 * How specifically a promotion's targets matched a cart line.
 * <p>
 * The ordinal encodes the precedence documented on
 * {@link com.harmoni.pos.menu.model.PromotionTarget}: a SKU target is the most
 * specific and therefore wins over a product target, which in turn wins over a
 * category target. A higher {@link #rank()} is a more specific match.
 * <p>
 * Specificity is a tie-breaker only. It never overrides
 * {@link com.harmoni.pos.menu.model.Promotion#priority}, which remains the primary
 * ordering signal; specificity merely decides between two promotions that would
 * otherwise grant the same discount on the same line.
 *
 * @author husainahmad
 */
public enum TargetMatch {

    /** No target on the promotion covers the line. */
    NONE(0),

    /** Matched every line of a category. */
    CATEGORY(1),

    /** Matched a specific product. */
    PRODUCT(2),

    /** Matched a specific SKU. */
    SKU(3);

    private final int rank;

    TargetMatch(int rank) {
        this.rank = rank;
    }

    /**
     * The precedence rank, higher being more specific.
     *
     * @return the precedence rank
     */
    public int rank() {
        return rank;
    }

    /**
     * Whether this match actually covers a line.
     *
     * @return true when the match is not {@link #NONE}
     */
    public boolean isMatch() {
        return this != NONE;
    }

    /**
     * The most specific of two matches.
     *
     * @param other the match to compare against, may be null
     * @return whichever of the two is more specific, treating null as {@link #NONE}
     */
    public TargetMatch mostSpecific(TargetMatch other) {
        if (other == null) {
            return this;
        }
        return rank >= other.rank ? this : other;
    }
}
