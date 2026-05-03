package com.w2m.virtual.risk.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable accumulator que las {@code RiskRule} usan para sumar score, añadir reasons
 * y/o forzar DENY. {@link com.w2m.virtual.risk.application.service.RiskService}
 * lo consolida en un {@link Assessment} inmutable.
 */
public final class AssessmentBuilder {
    private int score = 0;
    private boolean forceDeny = false;
    private final List<String> reasons = new ArrayList<>();

    public void addScore(int delta) {
        this.score += delta;
    }

    public void addReason(String reason) {
        if (reason != null && !reason.isBlank()) {
            this.reasons.add(reason);
        }
    }

    public void forceDeny() {
        this.forceDeny = true;
    }

    public int score() { return score; }
    public boolean isForceDeny() { return forceDeny; }
    public List<String> reasons() { return List.copyOf(reasons); }
    public boolean hasAnySignal() { return forceDeny || score > 0 || !reasons.isEmpty(); }
}
