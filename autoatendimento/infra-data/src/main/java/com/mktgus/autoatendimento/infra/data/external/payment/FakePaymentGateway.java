package com.mktgus.autoatendimento.infra.data.external.payment;

import com.mktgus.autoatendimento.application.gateway.PaymentGateway;
import com.mktgus.autoatendimento.application.payment.PaymentConfig;
import com.mktgus.autoatendimento.application.payment.PaymentProviderResult;
import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentStatus;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "fake", matchIfMissing = true)
public class FakePaymentGateway implements PaymentGateway {
    private final PaymentConfig paymentConfig;

    public FakePaymentGateway(PaymentConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    @Override
    public PaymentProviderResult start(PaymentMethod method, double amount) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentProviderResult(
                "fake",
                "fake-" + UUID.randomUUID(),
                PaymentStatus.PROCESSING,
                null,
                now.plusMinutes(paymentConfig.getExpirationMinutes()),
                null
        );
    }

    @Override
    public PaymentProviderResult refresh(PaymentTransaction transaction) {
        if (transaction.status() == PaymentStatus.PAID || transaction.status() == PaymentStatus.FAILED || transaction.status() == PaymentStatus.CANCELED || transaction.status() == PaymentStatus.EXPIRED) {
            return new PaymentProviderResult(
                    transaction.provider(),
                    transaction.providerReference(),
                    transaction.status(),
                    transaction.failureReason(),
                    transaction.expiresAt(),
                    transaction.confirmedAt()
            );
        }

        LocalDateTime now = LocalDateTime.now();
        if (transaction.expiresAt() != null && now.isAfter(transaction.expiresAt())) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.EXPIRED, "Pagamento expirado.", transaction.expiresAt(), null);
        }

        if (Duration.between(transaction.createdAt(), now).toMillis() >= paymentConfig.getFakeApprovalDelayMs()) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.PAID, null, transaction.expiresAt(), now);
        }

        return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.PROCESSING, null, transaction.expiresAt(), null);
    }

    @Override
    public PaymentProviderResult confirm(PaymentTransaction transaction) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.PAID, null, transaction.expiresAt(), now);
    }
}
