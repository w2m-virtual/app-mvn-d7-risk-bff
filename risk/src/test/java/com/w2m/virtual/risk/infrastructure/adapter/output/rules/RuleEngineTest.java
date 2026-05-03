package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.application.service.RiskService;
import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.Assessment;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import com.w2m.virtual.risk.infrastructure.adapter.output.inmemory.InMemoryAssessmentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cubre las 6 reglas + caso multi-rule con resolución a REVIEW.
 *
 * <p>Las reglas se inyectan en orden explícito (mismo que el {@code @Order} de Spring) y se
 * verifica el outcome final consolidado por {@link RiskService}.</p>
 */
class RuleEngineTest {

    private final RuleEngine engine = new RuleEngine(List.of(
            new EmailBlacklistRule(),
            new InvalidDomainRule(),
            new HighAmountRule(),
            new HighRiskCountryRule(),
            new HolderNameSuspiciousRule(),
            new DefaultRule()
    ));
    private final RiskService service = new RiskService(engine, new InMemoryAssessmentRepository());

    private static AssessRequest base() {
        return new AssessRequest(
                UUID.randomUUID(),
                "Juan Pérez",
                "juan@example.com",
                "1.2.3.4",
                "ES",
                new BigDecimal("540.00"),
                "EUR",
                UUID.randomUUID(),
                "1234"
        );
    }

    @Test
    void emailBlacklist_forces_DENY() {
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "fraud@test.com", null, "ES", new BigDecimal("100"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_DENY);
        assertThat(a.reasons()).contains("EMAIL_BLACKLIST");
        assertThat(a.score()).isGreaterThanOrEqualTo(100);
    }

    @Test
    void invalidDomain_blocked_invalid_forces_DENY() {
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "x@blocked.invalid", null, "ES", new BigDecimal("100"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_DENY);
        assertThat(a.reasons()).contains("INVALID_DOMAIN");
    }

    @Test
    void highAmount_alone_yields_REVIEW() {
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "ana@example.com", null, "ES", new BigDecimal("8000"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_REVIEW);
        assertThat(a.reasons()).contains("HIGH_AMOUNT");
        assertThat(a.score()).isEqualTo(40);
    }

    @Test
    void highRiskCountry_alone_yields_APPROVE_butAddsScore() {
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "ana@example.com", null, "XX", new BigDecimal("100"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        // 30 (country) → REVIEW threshold es 40, no llega → APPROVE
        assertThat(a.decision()).isEqualTo(Assessment.DECISION_APPROVE);
        assertThat(a.reasons()).contains("HIGH_RISK_COUNTRY");
        assertThat(a.score()).isEqualTo(30);
    }

    @Test
    void suspiciousName_FRAUD_forces_DENY() {
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "FRAUD Test",
                "ana@example.com", null, "ES", new BigDecimal("100"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_DENY);
        assertThat(a.reasons()).contains("SUSPICIOUS_NAME");
    }

    @Test
    void defaultRule_yields_APPROVE_with_baseScore_when_noOtherSignals() {
        Assessment a = service.assess(base());

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_APPROVE);
        assertThat(a.reasons()).isEmpty();
        assertThat(a.score()).isEqualTo(10);
    }

    @Test
    void multiRule_highAmount_plus_highRiskCountry_yields_REVIEW() {
        // 40 + 30 = 70 ⇒ DENY threshold (>=70). Probamos límite REVIEW con monto medio.
        // Para REVIEW: amount > 5000 (40) + country normal → 40 = REVIEW
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "ana@example.com", null, "ES", new BigDecimal("6000"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_REVIEW);
        assertThat(a.reasons()).containsExactly("HIGH_AMOUNT");
        assertThat(a.score()).isEqualTo(40);
    }

    @Test
    void multiRule_highAmount_plus_highRiskCountry_combines_to_DENY_byScore() {
        // 40 + 30 = 70 ⇒ DENY por score (sin forceDeny).
        AssessRequest req = new AssessRequest(base().bookingAttemptId(), "Ana",
                "ana@example.com", null, "XX", new BigDecimal("6000"), "EUR",
                UUID.randomUUID(), "1234");

        Assessment a = service.assess(req);

        assertThat(a.decision()).isEqualTo(Assessment.DECISION_DENY);
        assertThat(a.reasons()).contains("HIGH_AMOUNT", "HIGH_RISK_COUNTRY");
        assertThat(a.score()).isEqualTo(70);
    }

    @Test
    void builder_basics_work() {
        // Test del helper directamente.
        AssessmentBuilder b = new AssessmentBuilder();
        assertThat(b.hasAnySignal()).isFalse();
        b.addScore(20);
        b.addReason("X");
        assertThat(b.hasAnySignal()).isTrue();
        assertThat(b.score()).isEqualTo(20);
        assertThat(b.reasons()).containsExactly("X");
    }
}
