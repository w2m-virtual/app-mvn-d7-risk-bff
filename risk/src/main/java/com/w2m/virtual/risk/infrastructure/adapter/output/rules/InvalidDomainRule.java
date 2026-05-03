package com.w2m.virtual.risk.infrastructure.adapter.output.rules;

import com.w2m.virtual.risk.domain.AssessRequest;
import com.w2m.virtual.risk.domain.AssessmentBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Regla 2: si el email termina en {@code @blocked.invalid} (cualquier local-part) o en
 * dominio TLD {@code .test} → DENY directo (score 100).
 *
 * <p>Esta regla habilita el smoke/Playwright test del DENY usando direcciones tipo
 * {@code x@blocked.invalid}.</p>
 */
@Component
@Order(20)
public class InvalidDomainRule implements RiskRule {

    @Override
    public void evaluate(AssessRequest req, AssessmentBuilder b) {
        if (req.holderEmail() == null) return;
        String email = req.holderEmail().trim().toLowerCase();
        int at = email.indexOf('@');
        if (at < 0 || at == email.length() - 1) return;
        String domain = email.substring(at + 1);
        if (domain.equals("blocked.invalid")
                || domain.endsWith(".blocked.invalid")
                || domain.equals("test")
                || domain.endsWith(".test")) {
            b.addScore(100);
            b.addReason("INVALID_DOMAIN");
            b.forceDeny();
        }
    }
}
