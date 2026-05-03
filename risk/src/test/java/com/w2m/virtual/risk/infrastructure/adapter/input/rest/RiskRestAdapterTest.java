package com.w2m.virtual.risk.infrastructure.adapter.input.rest;

import com.w2m.virtual.risk.application.port.input.AssessRiskInputPort;
import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.Assessment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RiskRestAdapter.class)
@Import(RiskRestAdapter.class)
class RiskRestAdapterTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean AssessRiskInputPort service;

    @Test
    void POST_assess_returns_200_with_decision() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        UUID bookingAttemptId = UUID.randomUUID();
        when(service.assess(any(AssessRequest.class))).thenReturn(new Assessment(
                assessmentId, bookingAttemptId, Assessment.DECISION_APPROVE, 12,
                List.of(), Instant.now()));

        String body = """
                {
                  "bookingAttemptId": "%s",
                  "holderName": "Juan",
                  "holderEmail": "juan@example.com",
                  "holderIp": "1.2.3.4",
                  "country": "ES",
                  "amount": 540.00,
                  "currency": "EUR",
                  "hotelId": "%s",
                  "paymentLast4": "1234"
                }
                """.formatted(bookingAttemptId, UUID.randomUUID());

        mvc.perform(post("/api/risk/assess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assessmentId").value(assessmentId.toString()))
                .andExpect(jsonPath("$.decision").value("APPROVE"))
                .andExpect(jsonPath("$.score").value(12));
    }

    @Test
    void POST_assess_returns_400_when_email_invalid() throws Exception {
        String body = """
                {
                  "bookingAttemptId": "%s",
                  "holderName": "Juan",
                  "holderEmail": "not-an-email",
                  "amount": 540.00,
                  "currency": "EUR",
                  "hotelId": "%s",
                  "paymentLast4": "1234"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mvc.perform(post("/api/risk/assess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
