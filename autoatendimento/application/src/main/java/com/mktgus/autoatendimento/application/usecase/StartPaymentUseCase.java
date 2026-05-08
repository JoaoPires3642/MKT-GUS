package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.PaymentGateway;
import com.mktgus.autoatendimento.application.gateway.PaymentTransactionGateway;
import com.mktgus.autoatendimento.application.payment.StartPaymentInput;
import com.mktgus.autoatendimento.application.payment.StartPaymentOutput;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StartPaymentUseCase {
    private final PaymentGateway paymentGateway;
    private final PaymentTransactionGateway paymentTransactionGateway;

    public StartPaymentUseCase(PaymentGateway paymentGateway, PaymentTransactionGateway paymentTransactionGateway) {
        this.paymentGateway = paymentGateway;
        this.paymentTransactionGateway = paymentTransactionGateway;
    }

    public StartPaymentOutput execute(StartPaymentInput input) {
        if (input.method() == null) {
            throw new ValidationException("Metodo de pagamento obrigatorio.");
        }

        if (input.amount() <= 0) {
            throw new ValidationException("Valor do pagamento deve ser positivo.");
        }

        var providerResult = paymentGateway.start(input.method(), input.amount());
        var now = LocalDateTime.now();
        PaymentTransaction transaction = new PaymentTransaction(
                null,
                providerResult.provider(),
                providerResult.providerReference(),
                input.method(),
                providerResult.status(),
                input.amount(),
                providerResult.failureReason(),
                providerResult.expiresAt(),
                providerResult.confirmedAt(),
                now,
                now,
                null
        );

        return new StartPaymentOutput(paymentTransactionGateway.save(transaction));
    }
}
