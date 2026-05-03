package com.w2m.virtual.risk.application.port.output;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;

/**
 * Puerto de salida: motor de reglas de riesgo. La implementación aplica todas las reglas
 * configuradas y rellena el {@link AssessmentBuilder}. La decisión final (DENY/REVIEW/APPROVE)
 * la consolida el servicio de aplicación a partir del builder.
 */
public interface RiskRuleEnginePort {
    AssessmentBuilder evaluate(AssessRequest request);
}
