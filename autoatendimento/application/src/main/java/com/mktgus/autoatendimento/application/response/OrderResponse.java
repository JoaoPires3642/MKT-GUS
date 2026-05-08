package com.mktgus.autoatendimento.application.response;

import com.mktgus.autoatendimento.domain.model.TaxDocumentStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Versão atualizada — inclui informações do documento fiscal no response da venda.
 * Substitui o record original em application/response/OrderResponse.java
 */
public record OrderResponse(
        Long id,
        Long customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        List<OrderItemResponse> items,
        Integer updatedPointsBalance,
        TaxDocumentResponse taxDocument
) {
    public record TaxDocumentResponse(
            TaxDocumentStatus status,
            String numeroDocumento,
            String chaveAcesso,
            String urlDanfe,
            String motivoFalha
    ) {
    }
}