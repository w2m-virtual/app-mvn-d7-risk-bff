package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Regla 3: si {@code amount > 5000} → +40 score (REVIEW por sí sola). No fuerza DENY. */
@Component
@Order(30)
public class HighAmountRule implements RiskRule {

    static final BigDecimal THRESHOLD = new BigDecimal("5000");

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (req.amount() == null) return;
        if (req.amount().compareTo(THRESHOLD) > 0) {
            b.addScore(40);
            b.addReason("HIGH_AMOUNT");
        }
    }
}
