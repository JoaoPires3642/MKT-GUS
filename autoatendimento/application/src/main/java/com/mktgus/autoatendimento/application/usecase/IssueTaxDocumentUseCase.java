package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.tax.TaxEmissionResult;
import com.mktgus.autoatendimento.application.gateway.TaxDocumentGateway;
import com.mktgus.autoatendimento.application.gateway.TaxEmissionGateway;
import com.mktgus.autoatendimento.domain.model.TaxDocument;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.TaxDocumentStatus;
import com.mktgus.autoatendimento.domain.model.TaxDocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Orquestra a emissão do documento fiscal para um pedido já salvo.
 *
 * Regra central: emissão só ocorre DEPOIS do pedido estar persistido
 * (chamado a partir de ConfirmPurchaseUseCase após orderGateway.save()).
 *
 * Falhas de emissão NÃO revertem o pedido — são registradas com
 * status FAILED e podem ser reprocessadas.
 */
@Service
public class IssueTaxDocumentUseCase {

    private static final Logger log = LoggerFactory.getLogger(IssueTaxDocumentUseCase.class);

    private final TaxDocumentGateway taxDocumentGateway;
    private final TaxEmissionGateway taxEmissionGateway;

    public IssueTaxDocumentUseCase(
            TaxDocumentGateway taxDocumentGateway,
            TaxEmissionGateway taxEmissionGateway
    ) {
        this.taxDocumentGateway = taxDocumentGateway;
        this.taxEmissionGateway = taxEmissionGateway;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TaxDocument execute(Order order, TaxDocumentType type) {

        TaxDocument pending = taxDocumentGateway.save(
                TaxDocument.pending(order.id(), type)
        );

        TaxDocument processing = taxDocumentGateway.save(
                updateStatus(pending, TaxDocumentStatus.PROCESSING)
        );

        TaxEmissionResult result;
        try {
            result = taxEmissionGateway.emit(order);
        } catch (Exception ex) {
            log.error("Erro inesperado ao chamar integradora fiscal para pedido {}: {}", order.id(), ex.getMessage(), ex);
            result = TaxEmissionResult.failure("Erro interno ao chamar integradora: " + ex.getMessage());
        }

        TaxDocument finalized = applyResult(processing, result);
        TaxDocument saved = taxDocumentGateway.save(finalized);

        if (!result.success()) {
            log.warn("Emissão fiscal FALHOU para pedido {}. Motivo: {}. Tentativas: {}",
                    order.id(), result.failureReason(), saved.attempts());
        } else {
            log.info("Documento fiscal emitido com sucesso. Pedido: {}, Chave: {}",
                    order.id(), saved.accessKey());
        }

        return saved;
    }

    private TaxDocument applyResult(TaxDocument doc, TaxEmissionResult result) {
        int newAttempts = doc.attempts() + 1;

        if (result.success()) {
            return new TaxDocument(
                    doc.id(),
                    doc.orderId(),
                    TaxDocumentStatus.ISSUED,
                    doc.type(),
                    result.documentNumber(),
                    result.accessKey(),
                    result.danfeUrl(),
                    null,
                    newAttempts,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }

        TaxDocumentStatus status = newAttempts >= 3
                ? TaxDocumentStatus.CANCELED
                : TaxDocumentStatus.FAILED;

        return new TaxDocument(
                doc.id(),
                doc.orderId(),
                status,
                doc.type(),
                null,
                null,
                null,
                result.failureReason(),
                newAttempts,
                null,
                LocalDateTime.now()
        );
    }

    private TaxDocument updateStatus(TaxDocument doc, TaxDocumentStatus newStatus) {
        return new TaxDocument(
                doc.id(),
                doc.orderId(),
                newStatus,
                doc.type(),
                doc.documentNumber(),
                doc.accessKey(),
                doc.danfeUrl(),
                doc.failureReason(),
                doc.attempts(),
                doc.issuedAt(),
                LocalDateTime.now()
        );
    }
}