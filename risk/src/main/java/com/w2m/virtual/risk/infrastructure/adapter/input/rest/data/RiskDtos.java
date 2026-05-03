package com.w2m.virtual.risk.infrastructure.adapter.input.rest.data;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.Assessment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** DTOs del subdominio risk — endpoints {@code /api/risk/*}. */
public final class RiskDtos {

    private RiskDtos() {}

    public record AssessRiskRequest(
            @NotNull UUID bookingAttemptId,
            @NotBlank String holderName,
            @NotBlank @Email String holderEmail,
            String holderIp,
            String country,
            @NotNull BigDecimal amount,
            @NotBlank String currency,
            @NotNull UUID hotelId,
            @NotBlank String paymentLast4
    ) {
        public AssessRequest toDomain() {
            return new AssessRequest(bookingAttemptId, holderName, holderEmail, holderIp,
                    country, amount, currency, hotelId, paymentLast4);
        }
    }

    public record AssessRiskResponse(
            UUID assessmentId,
            String decision,
            int score,
            List<String> reasons,
            Instant assessedAt
    ) {
        public static AssessRiskResponse from(Assessment a) {
            return new AssessRiskResponse(a.assessmentId(), a.decision(), a.score(),
                    a.reasons(), a.assessedAt());
        }
    }

    public record AssessmentResponse(
            UUID assessmentId,
            UUID bookingAttemptId,
            String decision,
            int score,
            List<String> reasons,
            Instant assessedAt
    ) {
        public static AssessmentResponse from(Assessment a) {
            return new AssessmentResponse(a.assessmentId(), a.bookingAttemptId(),
                    a.decision(), a.score(), a.reasons(), a.assessedAt());
        }
    }
}
