package com.mktgus.autoatendimento.application.gateway;
 
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.application.tax.TaxEmissionResult;
 
/**
 * Contrato para qualquer integradora fiscal (Focus NFe, SAT, etc.).
 * A implementação fica em infra-data; o use case não sabe qual é usada.
 */
public interface TaxEmissionGateway {
    TaxEmissionResult emit(Order order);
}