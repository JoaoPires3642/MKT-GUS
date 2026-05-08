package com.mktgus.autoatendimento.application.response;

import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String provider,
        String providerReference,
        PaymentMethod method,
        PaymentStatus status,
        double amount,
        String failureReason,
        LocalDateTime expiresAt,
        LocalDateTime confirmedAt
) {
}
