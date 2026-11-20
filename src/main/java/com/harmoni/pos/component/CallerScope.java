package com.harmoni.pos.component;

import com.harmoni.pos.exception.ScopeAccessDeniedException;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable view of the organisation ids a caller may access, resolved from the
 * {@code X-Scopes} header injected by the gateway.
 *
 * <p>A {@code BRAND} scope grants the brand and everything under it, a
 * {@code CHAIN} scope grants the chain and its stores, and a {@code STORE} scope
 * grants only that store. The sets are already expanded, so callers only need
 * the {@code canAccess*} checks or the {@code filter*} helpers.</p>
 */
public final class CallerScope {

    private static final CallerScope GLOBAL = new CallerScope(true, Set.of(), Set.of(), Set.of());
    private static final CallerScope NONE = new CallerScope(false, Set.of(), Set.of(), Set.of());

    private final boolean global;
    private final Set<Integer> brandIds;
    private final Set<Integer> chainIds;
    private final Set<Integer> storeIds;

    private CallerScope(boolean global, Set<Integer> brandIds, Set<Integer> chainIds, Set<Integer> storeIds) {
        this.global = global;
        this.brandIds = Collections.unmodifiableSet(new LinkedHashSet<>(brandIds));
        this.chainIds = Collections.unmodifiableSet(new LinkedHashSet<>(chainIds));
        this.storeIds = Collections.unmodifiableSet(new LinkedHashSet<>(storeIds));
    }

    /**
     * A scope that can see everything. Used for internal calls that carry no
     * scope header, preserving the legacy behaviour.
     *
     * @return the unrestricted scope
     */
    public static CallerScope global() {
        return GLOBAL;
    }

    /**
     * A scope that can see nothing. Used when a scope header is present but
     * carries no recognised assignment, so a malformed header cannot widen
     * access.
     *
     * @return the empty scope
     */
    public static CallerScope none() {
        return NONE;
    }

    /**
     * Builds a scope from already-expanded id sets.
     *
     * @param brandIds the accessible brand ids
     * @param chainIds the accessible chain ids
     * @param storeIds the accessible store ids
     * @return the scope
     */
    public static CallerScope of(Set<Integer> brandIds, Set<Integer> chainIds, Set<Integer> storeIds) {
        return new CallerScope(false, brandIds, chainIds, storeIds);
    }

    public boolean isGlobal() {
        return global;
    }

    public Set<Integer> getBrandIds() {
        return brandIds;
    }

    public Set<Integer> getChainIds() {
        return chainIds;
    }

    public Set<Integer> getStoreIds() {
        return storeIds;
    }

    public boolean canAccessBrand(Integer brandId) {
        return global || brandId == null || brandIds.contains(brandId);
    }

    public boolean canAccessChain(Integer chainId) {
        return global || chainId == null || chainIds.contains(chainId);
    }

    public boolean canAccessStore(Integer storeId) {
        return global || storeId == null || storeIds.contains(storeId);
    }

    /**
     * Asserts that the caller may access the brand.
     *
     * @param brandId the brand id
     * @throws ScopeAccessDeniedException when the brand is outside the scope
     */
    public void requireBrand(Integer brandId) {
        if (!canAccessBrand(brandId)) {
            throw new ScopeAccessDeniedException("Brand " + brandId + " is outside the caller's scope");
        }
    }

    /**
     * Asserts that the caller may access the chain.
     *
     * @param chainId the chain id
     * @throws ScopeAccessDeniedException when the chain is outside the scope
     */
    public void requireChain(Integer chainId) {
        if (!canAccessChain(chainId)) {
            throw new ScopeAccessDeniedException("Chain " + chainId + " is outside the caller's scope");
        }
    }

    /**
     * Asserts that the caller may access the store.
     *
     * @param storeId the store id
     * @throws ScopeAccessDeniedException when the store is outside the scope
     */
    public void requireStore(Integer storeId) {
        if (!canAccessStore(storeId)) {
            throw new ScopeAccessDeniedException("Store " + storeId + " is outside the caller's scope");
        }
    }

    /**
     * Asserts that the caller may access the brand of an entity.
     *
     * @param brandId the brand id the entity belongs to
     * @param entity the entity label used in the error message
     * @param id the entity id used in the error message
     * @throws ScopeAccessDeniedException when the brand is outside the scope
     */
    public void requireBrandOf(Integer brandId, String entity, Object id) {
        if (!canAccessBrand(brandId)) {
            throw new ScopeAccessDeniedException(entity + " " + id + " is outside the caller's scope");
        }
    }

    /**
     * Keeps only the brands the caller may see.
     *
     * @param brands the candidate brands
     * @return the accessible brands, never {@code null}
     */
    public List<Brand> filterBrands(List<Brand> brands) {
        if (brands == null) {
            return new ArrayList<>();
        }
        if (global) {
            return new ArrayList<>(brands);
        }
        return brands.stream()
                .filter(brand -> brand != null && canAccessBrand(brand.getId()))
                .toList();
    }

    /**
     * Keeps only the chains the caller may see.
     *
     * @param chains the candidate chains
     * @return the accessible chains, never {@code null}
     */
    public List<Chain> filterChains(List<Chain> chains) {
        if (chains == null) {
            return new ArrayList<>();
        }
        if (global) {
            return new ArrayList<>(chains);
        }
        return chains.stream()
                .filter(chain -> chain != null && canAccessChain(chain.getId()))
                .toList();
    }

    /**
     * Keeps only the stores the caller may see.
     *
     * @param stores the candidate stores
     * @return the accessible stores, never {@code null}
     */
    public List<Store> filterStores(List<Store> stores) {
        if (stores == null) {
            return new ArrayList<>();
        }
        if (global) {
            return new ArrayList<>(stores);
        }
        return stores.stream()
                .filter(store -> store != null && canAccessStore(store.getId()))
                .toList();
    }

    /**
     * Intersects the requested store ids with the caller's accessible stores.
     *
     * @param requested the requested store ids, may be {@code null}
     * @return the allowed store ids, never {@code null}
     */
    public List<Integer> filterStoreIds(Collection<Integer> requested) {
        if (requested == null) {
            return new ArrayList<>();
        }
        if (global) {
            return new ArrayList<>(requested.stream().filter(Objects::nonNull).distinct().toList());
        }
        return new ArrayList<>(requested.stream()
                .filter(Objects::nonNull)
                .filter(storeIds::contains)
                .distinct()
                .toList());
    }
}
