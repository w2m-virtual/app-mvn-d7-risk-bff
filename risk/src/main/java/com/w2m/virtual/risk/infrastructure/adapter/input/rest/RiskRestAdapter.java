package com.w2m.virtual.risk.infrastructure.adapter.input.rest;

import com.w2m.virtual.risk.application.port.input.AssessRiskInputPort;
import com.w2m.virtual.risk.domain.Assessment;
import com.w2m.virtual.risk.infrastructure.adapter.input.rest.data.RiskDtos.AssessRiskRequest;
import com.w2m.virtual.risk.infrastructure.adapter.input.rest.data.RiskDtos.AssessRiskResponse;
import com.w2m.virtual.risk.infrastructure.adapter.input.rest.data.RiskDtos.AssessmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Endpoints {@code /api/risk/*}. */
@RestController
@RequestMapping("/api/risk")
public class RiskRestAdapter {

    private final AssessRiskInputPort service;

    public RiskRestAdapter(AssessRiskInputPort service) {
        this.service = service;
    }

    @PostMapping("/assess")
    public ResponseEntity<AssessRiskResponse> assess(@Valid @RequestBody AssessRiskRequest req) {
        Assessment a = service.assess(req.toDomain());
        return ResponseEntity.ok(AssessRiskResponse.from(a));
    }

    @GetMapping("/assessments/{id}")
    public ResponseEntity<AssessmentResponse> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(AssessmentResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
