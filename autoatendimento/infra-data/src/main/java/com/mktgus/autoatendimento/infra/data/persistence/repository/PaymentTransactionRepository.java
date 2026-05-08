package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.infra.data.persistence.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {
}
