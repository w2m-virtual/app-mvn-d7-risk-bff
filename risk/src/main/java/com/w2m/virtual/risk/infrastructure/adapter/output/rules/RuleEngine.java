package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.application.port.output.RiskRuleEnginePort;
import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Aplica todas las {@link RiskRule} configuradas en orden (Spring inyecta la lista
 * ya ordenada por {@code @Order}). Cada regla suma señales al builder; el servicio
 * de aplicación consolida la decisión final.
 */
@Component
public class RuleEngine implements RiskRuleEnginePort {

    private final List<RiskRule> rules;

    public RuleEngine(List<RiskRule> rules) {
        this.rules = rules;
    }

    @Override
    public AssessmentBuilder evaluate(AssessRequest request) {
        AssessmentBuilder b = new AssessmentBuilder();
        for (RiskRule rule : rules) {
            rule.evaluate(request, b);
        }
        return b;
    }
}
