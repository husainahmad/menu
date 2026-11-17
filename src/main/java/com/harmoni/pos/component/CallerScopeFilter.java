package com.harmoni.pos.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resolves the caller's organisation scope once per request and publishes it on
 * {@link CallerScopeContext}.
 *
 * <p>The header is expanded here rather than in each controller so every
 * endpoint sees the same scope and the lookup queries run at most once. A
 * failure while expanding is turned into an empty scope: a broken lookup must
 * never silently widen access.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class CallerScopeFilter extends OncePerRequestFilter {

    private final CallerScopeService callerScopeService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            CallerScopeContext.set(resolve(request));
            filterChain.doFilter(request, response);
        } finally {
            CallerScopeContext.clear();
        }
    }

    private CallerScope resolve(HttpServletRequest request) {
        try {
            return callerScopeService.resolve(request.getHeader(CallerScopeService.HEADER_SCOPES));
        } catch (RuntimeException exception) {
            log.error("Could not resolve caller scope", exception);
            return CallerScope.none();
        }
    }
}
