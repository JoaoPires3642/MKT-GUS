package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.gateway.TaxDocumentGateway;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.model.TaxDocument;
import com.mktgus.autoatendimento.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reprocessa documentos fiscais em estado FAILED que ainda têm tentativas restantes.
 * Pode ser invocado por scheduler (ex: @Scheduled) ou endpoint administrativo.
 *
 * Requer que OrderGateway exponha findById — adicionar método à interface.
 */
@Service
public class ReprocessTaxDocumentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReprocessTaxDocumentUseCase.class);

    private final TaxDocumentGateway taxDocumentGateway;
    private final OrderGateway orderGateway;
    private final IssueTaxDocumentUseCase issueTaxDocumentUseCase;

    public ReprocessTaxDocumentUseCase(
            TaxDocumentGateway taxDocumentGateway,
            OrderGateway orderGateway,
            IssueTaxDocumentUseCase issueTaxDocumentUseCase
    ) {
        this.taxDocumentGateway = taxDocumentGateway;
        this.orderGateway = orderGateway;
        this.issueTaxDocumentUseCase = issueTaxDocumentUseCase;
    }

    public void execute() {
        List<TaxDocument> pending = taxDocumentGateway.findPendingForRetry();
        log.info("Reprocessamento fiscal: {} documentos em fila.", pending.size());

        for (TaxDocument doc : pending) {
            try {
                Order order = orderGateway.findById(doc.orderId())
                        .orElseThrow(() -> new IllegalStateException("Pedido não encontrado: " + doc.orderId()));

                issueTaxDocumentUseCase.execute(order, doc.type());

            } catch (Exception ex) {
                log.error("Erro ao reprocessar documento fiscal id={}: {}", doc.id(), ex.getMessage(), ex);
            }
        }
    }
}