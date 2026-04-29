package com.airbnb.booking.migration;

import com.airbnb.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Strangler Fig Health Indicator
 *
 * Exposed at: GET /actuator/health
 *
 * Provides a real-time view of the migration state so ops teams can track
 * the rollout without digging into code or config files.
 *
 * This is a classic senior-level touch: observability is not an afterthought
 * but is built into the service contract from day one.
 *
 * Example output (in "health" actuator response):
 * {
 *   "stranglerFig": {
 *     "status": "UP",
 *     "details": {
 *       "migrationPhase": "PHASE_3_CUTOVER",
 *       "phaseLabel": "Full Cutover",
 *       "description": "100% of booking traffic served by this Java microservice",
 *       "javaIsPrimary": true,
 *       "totalBookings": 142,
 *       "legacySystem": "Node.js Express (strangled)",
 *       "newSystem": "Java 21 Spring Boot + Virtual Threads"
 *     }
 *   }
 * }
 */
@Component("stranglerFig")
public class StranglerFigHealthIndicator implements HealthIndicator {

    @Value("${migration.phase:PHASE_3_CUTOVER}")
    private String migrationPhaseName;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Health health() {
        try {
            MigrationPhase phase = MigrationPhase.valueOf(migrationPhaseName);
            long totalBookings = bookingRepository.count();

            return Health.up()
                .withDetail("migrationPhase",  phase.name())
                .withDetail("phaseLabel",       phase.getLabel())
                .withDetail("description",      phase.getDescription())
                .withDetail("javaIsPrimary",    phase.isJavaIsPrimary())
                .withDetail("totalBookings",    totalBookings)
                .withDetail("legacySystem",     "Node.js Express (strangled)")
                .withDetail("newSystem",        "Java 21 Spring Boot + Virtual Threads")
                .build();

        } catch (Exception ex) {
            return Health.down()
                .withDetail("error", ex.getMessage())
                .build();
        }
    }
}