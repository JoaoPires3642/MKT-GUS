package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.domain.model.TaxDocumentStatus;
import com.mktgus.autoatendimento.infra.data.persistence.entity.TaxDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaxDocumentRepository extends JpaRepository<TaxDocumentEntity, Long> {

    Optional<TaxDocumentEntity> findByOrderId(Long orderId);

    @Query("SELECT d FROM TaxDocumentEntity d WHERE d.status = :status AND d.attempts < 3")
    List<TaxDocumentEntity> findByStatusAndAttemptsLessThan(TaxDocumentStatus status);

    default List<TaxDocumentEntity> findPendingForRetry() {
        return findByStatusAndAttemptsLessThan(TaxDocumentStatus.FAILED);
    }
}