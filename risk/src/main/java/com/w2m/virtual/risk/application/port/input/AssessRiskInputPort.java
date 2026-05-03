package com.w2m.virtual.risk.application.port.input;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.Assessment;

import java.util.Optional;
import java.util.UUID;

/** Caso de uso D7: evaluar el riesgo de un intento de booking. */
public interface AssessRiskInputPort {
    Assessment assess(AssessRequest req);

    Optional<Assessment> findById(UUID assessmentId);
}
