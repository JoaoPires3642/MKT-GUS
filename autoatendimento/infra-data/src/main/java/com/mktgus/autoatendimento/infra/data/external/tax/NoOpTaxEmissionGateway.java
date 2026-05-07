package com.mktgus.autoatendimento.infra.data.external.tax;

import com.mktgus.autoatendimento.application.tax.TaxEmissionResult;
import com.mktgus.autoatendimento.application.gateway.TaxEmissionGateway;
import com.mktgus.autoatendimento.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementação stub — ativa quando fiscal.emission-enabled=false.
 *
 * Documenta o limite atual: não emite documento real, apenas loga.
 * Quando a integração estiver pronta, criar FocusNfeTaxEmissionGateway
 * (ou equivalente) e ativar com fiscal.emission-enabled=true.
 *
 * LIMITE DOCUMENTADO: enquanto este bean estiver ativo, nenhum
 * documento fiscal real é emitido. Operação está em modo de simulação.
 */
@Component
@ConditionalOnProperty(
        name = "fiscal.emission-enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class NoOpTaxEmissionGateway implements TaxEmissionGateway {

    private static final Logger log = LoggerFactory.getLogger(NoOpTaxEmissionGateway.class);

    @Override
    public TaxEmissionResult emit(Order order) {
        log.warn("[TAX-STUB] Emissão fiscal desabilitada. Pedido {} NÃO gerou documento fiscal real.", order.id());

        return TaxEmissionResult.failure(
                "Módulo fiscal não implementado. Reprocessar após integração."
        );
    }
}