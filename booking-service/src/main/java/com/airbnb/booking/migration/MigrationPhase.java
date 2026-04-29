package com.airbnb.booking.migration;

/**
 * Strangler Fig Migration Phases
 *
 * THE PATTERN (Senior Narrative):
 * The "Strangler Fig" (coined by Martin Fowler) describes a safe, incremental
 * strategy for migrating a legacy monolith to microservices—mirroring how a
 * strangler fig vine wraps a host tree, eventually replacing it entirely while
 * the original tree is still standing.
 *
 * OUR MIGRATION STORY:
 *   Legacy system:  Node.js Express monolith handles ALL domain logic including
 *                   bookings — a classic fat-controller anti-pattern. Under load
 *                   its single-threaded event loop serializes booking conflict
 *                   checks, leading to race conditions and double-bookings.
 *
 *   New system:     This Java 21 Spring Boot microservice extracts ONLY the
 *                   Booking & Availability bounded context. It exposes a clean
 *                   REST contract that the existing Node.js frontend calls
 *                   transparently, with NO changes required in the UI tier.
 *
 * PHASE LIFECYCLE:
 *   PHASE_1_SHADOW   — Java service deployed; both systems write bookings.
 *                      Java responses are discarded. Used to validate parity.
 *   PHASE_2_CANARY   — 10% of booking traffic routed to Java; 90% still Node.
 *                      Metrics compared in real time.
 *   PHASE_3_CUTOVER  — 100% of booking traffic served by Java.
 *                      Node.js booking code becomes dead code.
 *   PHASE_4_COMPLETE — Node.js booking controllers deleted. Migration done.
 *
 * WHY THIS PROVES SENIORITY:
 *   Any developer can rewrite a service. A senior engineer migrates it without
 *   downtime, without a flag day, and with measurable rollback at every step.
 */
public enum MigrationPhase {

    PHASE_1_SHADOW(
        "Shadow Mode",
        "Java handles requests in parallel; responses compared but not served to clients",
        false
    ),
    PHASE_2_CANARY(
        "Canary Rollout",
        "10% of traffic served by Java; parity metrics collected",
        true
    ),
    PHASE_3_CUTOVER(
        "Full Cutover",
        "100% of booking traffic served by this Java microservice",
        true
    ),
    PHASE_4_COMPLETE(
        "Migration Complete",
        "Node.js booking logic fully decommissioned; Java is sole source of truth",
        true
    );

    private final String label;
    private final String description;
    private final boolean javaIsPrimary;

    MigrationPhase(String label, String description, boolean javaIsPrimary) {
        this.label = label;
        this.description = description;
        this.javaIsPrimary = javaIsPrimary;
    }

    public String getLabel()            { return label; }
    public String getDescription()      { return description; }
    public boolean isJavaIsPrimary()    { return javaIsPrimary; }
}