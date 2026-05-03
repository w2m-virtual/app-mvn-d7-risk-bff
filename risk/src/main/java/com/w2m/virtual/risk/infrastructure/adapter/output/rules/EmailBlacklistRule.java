package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Regla 1: si el email está en la lista negra hardcoded → DENY directo (score 100). */
@Component
@Order(10)
public class EmailBlacklistRule implements RiskRule {

    static final Set<String> BLACKLIST = Set.of(
            "fraud@test.com",
            "blacklist@example.com"
    );

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (req.holderEmail() == null) return;
        String email = req.holderEmail().trim().toLowerCase();
        if (BLACKLIST.contains(email)) {
            b.addScore(100);
            b.addReason("EMAIL_BLACKLIST");
            b.forceDeny();
        }
    }
}
