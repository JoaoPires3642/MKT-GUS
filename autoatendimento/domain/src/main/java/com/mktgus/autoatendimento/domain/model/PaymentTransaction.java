package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;

public record PaymentTransaction(
        Long id,
        String provider,
        String providerReference,
        PaymentMethod method,
        PaymentStatus status,
        double amount,
        String failureReason,
        LocalDateTime expiresAt,
        LocalDateTime confirmedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long orderId
) {
    public boolean isConfirmed() {
        return status == PaymentStatus.AUTHORIZED || status == PaymentStatus.PAID;
    }

    public boolean isConsumed() {
        return orderId != null;
    }

    public PaymentTransaction withStatus(PaymentStatus newStatus, String newFailureReason, LocalDateTime newConfirmedAt, LocalDateTime now) {
        return new PaymentTransaction(
                id,
                provider,
                providerReference,
                method,
                newStatus,
                amount,
                newFailureReason,
                expiresAt,
                newConfirmedAt,
                createdAt,
                now,
                orderId
        );
    }

    public PaymentTransaction linkToOrder(Long linkedOrderId, LocalDateTime now) {
        return new PaymentTransaction(
                id,
                provider,
                providerReference,
                method,
                status,
                amount,
                failureReason,
                expiresAt,
                confirmedAt,
                createdAt,
                now,
                linkedOrderId
        );
    }
}
