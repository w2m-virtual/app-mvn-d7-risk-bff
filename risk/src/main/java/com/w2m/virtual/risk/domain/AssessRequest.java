package com.w2m.virtual.risk.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Comando de dominio: petición de evaluación de riesgo emitida por el orquestador
 * antes de pasar la reserva al supplier.
 *
 * <p>{@code holderIp} y {@code country} son opcionales (pueden venir null).</p>
 */
public record AssessRequest(
        UUID bookingAttemptId,
        String holderName,
        String holderEmail,
        String holderIp,
        String country,
        BigDecimal amount,
        String currency,
        UUID hotelId,
        String paymentLast4
) {}
