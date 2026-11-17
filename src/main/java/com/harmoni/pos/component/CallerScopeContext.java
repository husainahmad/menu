package com.harmoni.pos.component;

/**
 * Holds the {@link CallerScope} resolved by {@link CallerScopeFilter} for the
 * duration of one request.
 *
 * <p>Controllers and services read the scope through {@link #current()} instead
 * of resolving it themselves, so the header is parsed and expanded once per
 * request. Outside a request (scheduled work, tests) the context falls back to
 * the unrestricted legacy scope.</p>
 */
public final class CallerScopeContext {

    private static final ThreadLocal<CallerScope> HOLDER = new ThreadLocal<>();

    private CallerScopeContext() {
    }

    /**
     * Stores the scope for the current request.
     *
     * @param scope the resolved scope
     */
    public static void set(CallerScope scope) {
        HOLDER.set(scope == null ? CallerScope.global() : scope);
    }

    /**
     * Returns the scope of the current request.
     *
     * @return the caller scope, never {@code null}
     */
    public static CallerScope current() {
        CallerScope scope = HOLDER.get();
        return scope == null ? CallerScope.global() : scope;
    }

    /**
     * Clears the scope. Always called by the filter in a {@code finally} block so
     * a pooled request thread cannot leak one caller's scope into another.
     */
    public static void clear() {
        HOLDER.remove();
    }
}
