package com.mktgus.autoatendimento.application.payment;

import com.mktgus.autoatendimento.domain.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentProviderResult(
        String provider,
        String providerReference,
        PaymentStatus status,
        String failureReason,
        LocalDateTime expiresAt,
        LocalDateTime confirmedAt
) {
}
