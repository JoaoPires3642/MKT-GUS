package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.gateway.PaymentGateway;
import com.mktgus.autoatendimento.application.gateway.PaymentTransactionGateway;
import com.mktgus.autoatendimento.application.payment.PaymentStatusOutput;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GetPaymentStatusUseCase {
    private final PaymentGateway paymentGateway;
    private final PaymentTransactionGateway paymentTransactionGateway;

    public GetPaymentStatusUseCase(PaymentGateway paymentGateway, PaymentTransactionGateway paymentTransactionGateway) {
        this.paymentGateway = paymentGateway;
        this.paymentTransactionGateway = paymentTransactionGateway;
    }

    public PaymentStatusOutput execute(Long transactionId) {
        PaymentTransaction transaction = paymentTransactionGateway.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transacao de pagamento nao encontrada: " + transactionId));

        var providerResult = paymentGateway.refresh(transaction);
        PaymentTransaction updated = paymentTransactionGateway.save(
                transaction.withStatus(
                        providerResult.status(),
                        providerResult.failureReason(),
                        providerResult.confirmedAt(),
                        LocalDateTime.now()
                )
        );

        return new PaymentStatusOutput(updated);
    }
}
