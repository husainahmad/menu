package com.harmoni.pos.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuration for the shared {@link Clock}.
 * <p>
 * Anything that reads the current date or time must go through this bean rather than
 * calling {@code Instant.now()} or {@code LocalDate.now()} directly. The promotion
 * engine is the main consumer: it decides whether a happy hour is live and whether a
 * promotion's date range covers today, and both are impossible to test deterministically
 * without an injectable clock.
 * <p>
 * Tests override it with a {@link Clock#fixed} instance, which is why it is a bean
 * rather than instantiated at each call site.
 */
@Configuration
public class ClockConfig {

    /**
     * The system clock in the default zone. The promotion engine re-zones it per
     * evaluation from the store's own {@code PromotionContext}, so the zone here is only
     * a starting point.
     *
     * @return the system clock
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
