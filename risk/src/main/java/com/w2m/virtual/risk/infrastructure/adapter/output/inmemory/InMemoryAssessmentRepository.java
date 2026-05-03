package com.w2m.virtual.risk.infrastructure.adapter.output.inmemory;

import com.w2m.virtual.risk.application.port.output.AssessmentRepositoryOutputPort;
import com.w2m.virtual.risk.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio in-memory: {@link ConcurrentHashMap} indexada por assessmentId. */
@Component
public class InMemoryAssessmentRepository implements AssessmentRepositoryOutputPort {

    private final Map<UUID, Assessment> byId = new ConcurrentHashMap<>();

    @Override
    public Assessment save(Assessment assessment) {
        byId.put(assessment.assessmentId(), assessment);
        return assessment;
    }

    @Override
    public Optional<Assessment> findById(UUID assessmentId) {
        return Optional.ofNullable(byId.get(assessmentId));
    }
}
