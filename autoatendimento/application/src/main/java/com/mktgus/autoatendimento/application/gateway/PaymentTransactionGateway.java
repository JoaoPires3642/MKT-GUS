package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.domain.model.PaymentTransaction;

import java.util.Optional;

public interface PaymentTransactionGateway {
    PaymentTransaction save(PaymentTransaction transaction);
    Optional<PaymentTransaction> findById(Long id);
}
