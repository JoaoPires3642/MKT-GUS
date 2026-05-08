package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.domain.model.TaxDocument;

import java.util.List;
import java.util.Optional;

public interface TaxDocumentGateway {

    TaxDocument save(TaxDocument taxDocument);

    Optional<TaxDocument> findByOrderId(Long orderId);

    List<TaxDocument> findPendingForRetry();
}