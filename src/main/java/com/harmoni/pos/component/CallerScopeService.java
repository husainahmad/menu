package com.harmoni.pos.component;

import com.github.pagehelper.PageHelper;
import com.harmoni.pos.menu.mapper.ChainMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Expands the {@code X-Scopes} header the gateway forwards into the brand,
 * chain and store ids a caller may access.
 *
 * <p>A {@code BRAND} assignment covers every chain and store of the brand, a
 * {@code CHAIN} assignment covers the chain and its stores, and a {@code STORE}
 * assignment covers only that store. The result is published on
 * {@link CallerScopeContext} by {@link CallerScopeFilter}.</p>
 */
@Component
@RequiredArgsConstructor
public class CallerScopeService {

    public static final String HEADER_SCOPES = "X-Scopes";

    private static final String GLOBAL = "GLOBAL";
    private static final String BRAND = "BRAND";
    private static final String CHAIN = "CHAIN";
    private static final String STORE = "STORE";

    private final ChainMapper chainMapper;
    private final StoreMapper storeMapper;

    /**
     * Resolves a raw {@code X-Scopes} header value.
     *
     * @param header comma separated {@code TYPE:id} assignments, may be {@code null}
     * @return the resolved scope
     */
    public CallerScope resolve(String header) {
        if (header == null || header.isBlank()) {
            return CallerScope.global();
        }

        Set<Integer> brandScopes = new LinkedHashSet<>();
        Set<Integer> chainScopes = new LinkedHashSet<>();
        Set<Integer> storeScopes = new LinkedHashSet<>();
        boolean global = false;

        for (String token : header.split(",")) {
            String[] parts = token.trim().split(":");
            if (parts.length != 2) {
                continue;
            }
            Integer id = parseId(parts[1]);
            if (id == null) {
                continue;
            }
            switch (parts[0].trim().toUpperCase(Locale.ROOT)) {
                case GLOBAL -> global = true;
                case BRAND -> brandScopes.add(id);
                case CHAIN -> chainScopes.add(id);
                case STORE -> storeScopes.add(id);
                default -> {
                    // Unknown scope types are ignored rather than widening access.
                }
            }
        }

        if (global) {
            return CallerScope.global();
        }
        if (brandScopes.isEmpty() && chainScopes.isEmpty() && storeScopes.isEmpty()) {
            return CallerScope.none();
        }

        PageHelper.clearPage();

        Set<Integer> brandIds = new LinkedHashSet<>(brandScopes);
        Set<Integer> chainIds = new LinkedHashSet<>();
        Set<Integer> storeIds = new LinkedHashSet<>();

        for (Integer brandId : brandScopes) {
            for (Chain chain : chainMapper.selectByBrandId(brandId)) {
                addChainAndStores(chain, chainIds, storeIds);
            }
        }
        for (Integer chainId : chainScopes) {
            Chain chain = chainMapper.selectByPrimaryKey(chainId);
            if (chain == null) {
                continue;
            }
            if (chain.getBrandId() != null) {
                brandIds.add(chain.getBrandId());
            }
            addChainAndStores(chain, chainIds, storeIds);
        }
        for (Integer storeId : storeScopes) {
            Store store = storeMapper.selectByPrimaryKey(storeId);
            if (store == null) {
                continue;
            }
            storeIds.add(store.getId());
            if (store.getChainId() != null) {
                chainIds.add(store.getChainId());
                Chain chain = chainMapper.selectByPrimaryKey(store.getChainId());
                if (chain != null && chain.getBrandId() != null) {
                    brandIds.add(chain.getBrandId());
                }
            }
        }

        PageHelper.clearPage();
        return CallerScope.of(brandIds, chainIds, storeIds);
    }

    private void addChainAndStores(Chain chain, Set<Integer> chainIds, Set<Integer> storeIds) {
        if (chain == null || chain.getId() == null) {
            return;
        }
        chainIds.add(chain.getId());
        for (Store store : storeMapper.selectAllByChainId(chain.getId(), null, null)) {
            if (store != null && store.getId() != null) {
                storeIds.add(store.getId());
            }
        }
    }

    private static Integer parseId(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
