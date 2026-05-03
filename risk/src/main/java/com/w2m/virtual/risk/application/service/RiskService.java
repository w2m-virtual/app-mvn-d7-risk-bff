package com.w2m.virtual.risk.application.service;

import com.w2m.virtual.risk.application.port.input.AssessRiskInputPort;
import com.w2m.virtual.risk.application.port.output.AssessmentRepositoryOutputPort;
import com.w2m.virtual.risk.application.port.output.RiskRuleEnginePort;
import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.Assessment;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación D7 — orquesta motor de reglas + decisión final + persistencia.
 *
 * <p>Decisión final:
 * <ul>
 *   <li>Cualquier regla {@code forceDeny} → DENY.</li>
 *   <li>{@code score >= 70} → DENY.</li>
 *   <li>{@code score >= 40} → REVIEW.</li>
 *   <li>Resto → APPROVE.</li>
 * </ul>
 * </p>
 */
@Service
public class RiskService implements AssessRiskInputPort {

    private static final Logger log = LoggerFactory.getLogger(RiskService.class);

    static final int DENY_THRESHOLD = 70;
    static final int REVIEW_THRESHOLD = 40;

    private final RiskRuleEnginePort ruleEngine;
    private final AssessmentRepositoryOutputPort repository;

    public RiskService(RiskRuleEnginePort ruleEngine,
                       AssessmentRepositoryOutputPort repository) {
        this.ruleEngine = ruleEngine;
        this.repository = repository;
    }

    @Override
    public Assessment assess(AssessRequest req) {
        AssessmentBuilder builder = ruleEngine.evaluate(req);
        String decision = decide(builder);

        Assessment assessment = new Assessment(
                UUID.randomUUID(),
                req.bookingAttemptId(),
                decision,
                builder.score(),
                builder.reasons(),
                Instant.now()
        );

        log.info("Risk assessment {} bookingAttempt={} decision={} score={} reasons={}",
                assessment.assessmentId(), req.bookingAttemptId(),
                decision, builder.score(), builder.reasons());

        return repository.save(assessment);
    }

    @Override
    public Optional<Assessment> findById(UUID assessmentId) {
        return repository.findById(assessmentId);
    }

    private static String decide(AssessmentBuilder b) {
        if (b.isForceDeny() || b.score() >= DENY_THRESHOLD) {
            return Assessment.DECISION_DENY;
        }
        if (b.score() >= REVIEW_THRESHOLD) {
            return Assessment.DECISION_REVIEW;
        }
        return Assessment.DECISION_APPROVE;
    }
}
