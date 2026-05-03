package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;

/** SPI: una regla de riesgo. Suma score y/o reasons al builder, o fuerza DENY. */
public interface RiskRule {
    void evaluate(AssessRequest request, AssessmentBuilder builder);
}
