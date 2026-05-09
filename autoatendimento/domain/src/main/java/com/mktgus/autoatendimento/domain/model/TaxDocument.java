package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;

/**
 * Representa o documento fiscal associado a um pedido aprovado.
 * Gerado SOMENTE após pagamento confirmado.
 */
public record TaxDocument(
        Long id,
        Long orderId,
        TaxDocumentStatus status,
        TaxDocumentType type,
        String documentNumber,
        String accessKey,
        String danfeUrl,
        String failureReason,
        int attempts,
        LocalDateTime issuedAt,
        LocalDateTime lastAttemptAt
) {
    public boolean canRetry() {
        return status == TaxDocumentStatus.FAILED && attempts < 3;
    }

    public boolean isIssued() {
        return status == TaxDocumentStatus.ISSUED;
    }

    public static TaxDocument pending(Long orderId, TaxDocumentType type) {
        return new TaxDocument(
                null, orderId, TaxDocumentStatus.PENDING,
                type, null, null, null, null, 0, null, null
        );
    }
}