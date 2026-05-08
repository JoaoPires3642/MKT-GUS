package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.TaxDocumentGateway;
import com.mktgus.autoatendimento.domain.model.TaxDocument;
import com.mktgus.autoatendimento.infra.data.persistence.entity.TaxDocumentEntity;
import com.mktgus.autoatendimento.infra.data.persistence.repository.TaxDocumentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaTaxDocumentGateway implements TaxDocumentGateway {

    private final TaxDocumentRepository repository;

    public JpaTaxDocumentGateway(TaxDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public TaxDocument save(TaxDocument doc) {
        TaxDocumentEntity entity = toEntity(doc);
        TaxDocumentEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TaxDocument> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .map(this::toDomain);
    }

    @Override
    public List<TaxDocument> findPendingForRetry() {
        return repository.findPendingForRetry()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TaxDocumentEntity toEntity(TaxDocument doc) {
        TaxDocumentEntity e = new TaxDocumentEntity();
        e.setId(doc.id());
        e.setOrderId(doc.orderId());
        e.setStatus(doc.status());
        e.setType(doc.type());
        e.setDocumentNumber(doc.documentNumber());
        e.setAccessKey(doc.accessKey());
        e.setDanfeUrl(doc.danfeUrl());
        e.setFailureReason(doc.failureReason());
        e.setAttempts(doc.attempts());
        e.setIssuedAt(doc.issuedAt());
        e.setLastAttemptAt(doc.lastAttemptAt());
        return e;
    }

    private TaxDocument toDomain(TaxDocumentEntity e) {
        return new TaxDocument(
                e.getId(),
                e.getOrderId(),
                e.getStatus(),
                e.getType(),
                e.getDocumentNumber(),
                e.getAccessKey(),
                e.getDanfeUrl(),
                e.getFailureReason(),
                e.getAttempts(),
                e.getIssuedAt(),
                e.getLastAttemptAt()
        );
    }
}