package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Regla 6 (catch-all, último orden): si nadie marcó nada, añade un score base de 10
 * para reflejar el "ruido" mínimo de cualquier transacción. No genera reason.
 */
@Component
@Order(1000)
public class DefaultRule implements RiskRule {

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (!b.hasAnySignal()) {
            b.addScore(10);
        }
    }
}
