package com.w2m.virtual.risk.application.port.output;

import com.w2m.virtual.risk.domain.Assessment;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentRepositoryOutputPort {
    Assessment save(Assessment assessment);
    Optional<Assessment> findById(UUID assessmentId);
}
