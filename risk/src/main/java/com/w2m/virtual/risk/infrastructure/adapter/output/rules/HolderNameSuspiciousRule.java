package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Regla 5: si {@code holderName} contiene "FRAUD" (case-insensitive) → DENY directo. */
@Component
@Order(50)
public class HolderNameSuspiciousRule implements RiskRule {

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (req.holderName() == null) return;
        if (req.holderName().toUpperCase().contains("FRAUD")) {
            b.addScore(100);
            b.addReason("SUSPICIOUS_NAME");
            b.forceDeny();
        }
    }
}
