package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Regla 4: country en lista de alto riesgo → +30 score. No fuerza DENY por sí sola. */
@Component
@Order(40)
public class HighRiskCountryRule implements RiskRule {

    static final Set<String> HIGH_RISK = Set.of("XX", "YY");

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (req.country() == null) return;
        if (HIGH_RISK.contains(req.country().trim().toUpperCase())) {
            b.addScore(30);
            b.addReason("HIGH_RISK_COUNTRY");
        }
    }
}
