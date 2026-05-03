package com.w2m.virtual.risk.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resultado persistido de una evaluación de riesgo. {@code decision} ∈
 * {{@link #DECISION_APPROVE}, {@link #DECISION_REVIEW}, {@link #DECISION_DENY}}.
 */
public record Assessment(
        UUID assessmentId,
        UUID bookingAttemptId,
        String decision,
        int score,
        List<String> reasons,
        Instant assessedAt
) {
    public static final String DECISION_APPROVE = "APPROVE";
    public static final String DECISION_REVIEW = "REVIEW";
    public static final String DECISION_DENY = "DENY";
}
