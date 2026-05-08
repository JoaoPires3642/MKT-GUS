package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.PaymentTransactionGateway;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import com.mktgus.autoatendimento.infra.data.persistence.entity.PaymentTransactionEntity;
import com.mktgus.autoatendimento.infra.data.persistence.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaPaymentTransactionGateway implements PaymentTransactionGateway {
    private final PaymentTransactionRepository repository;

    public JpaPaymentTransactionGateway(PaymentTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        return toDomain(repository.save(toEntity(transaction)));
    }

    @Override
    public Optional<PaymentTransaction> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    private PaymentTransactionEntity toEntity(PaymentTransaction transaction) {
        PaymentTransactionEntity entity = new PaymentTransactionEntity();
        entity.setId(transaction.id());
        entity.setProvider(transaction.provider());
        entity.setProviderReference(transaction.providerReference());
        entity.setMethod(transaction.method());
        entity.setStatus(transaction.status());
        entity.setAmount(transaction.amount());
        entity.setFailureReason(transaction.failureReason());
        entity.setExpiresAt(transaction.expiresAt());
        entity.setConfirmedAt(transaction.confirmedAt());
        entity.setCreatedAt(transaction.createdAt());
        entity.setUpdatedAt(transaction.updatedAt());
        entity.setOrderId(transaction.orderId());
        return entity;
    }

    private PaymentTransaction toDomain(PaymentTransactionEntity entity) {
        return new PaymentTransaction(
                entity.getId(),
                entity.getProvider(),
                entity.getProviderReference(),
                entity.getMethod(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getFailureReason(),
                entity.getExpiresAt(),
                entity.getConfirmedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getOrderId()
        );
    }
}
