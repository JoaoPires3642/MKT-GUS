package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.gateway.PaymentGateway;
import com.mktgus.autoatendimento.application.gateway.PaymentTransactionGateway;
import com.mktgus.autoatendimento.application.payment.PaymentProviderResult;
import com.mktgus.autoatendimento.application.payment.StartPaymentInput;
import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentStatus;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentUseCasesTest {

    @Test
    void shouldStartPaymentSuccessfully() {
        InMemoryPaymentTransactionGateway gateway = new InMemoryPaymentTransactionGateway();
        StartPaymentUseCase useCase = new StartPaymentUseCase(new SuccessfulStartGateway(), gateway);

        var output = useCase.execute(new StartPaymentInput(PaymentMethod.PIX, 19.9));

        assertEquals(PaymentStatus.PROCESSING, output.transaction().status());
        assertEquals("fake-start", output.transaction().providerReference());
    }

    @Test
    void shouldReturnFailedPaymentStatus() {
        InMemoryPaymentTransactionGateway gateway = new InMemoryPaymentTransactionGateway();
        PaymentTransaction saved = gateway.save(transaction(PaymentStatus.PROCESSING, null));
        GetPaymentStatusUseCase useCase = new GetPaymentStatusUseCase(new FailedRefreshGateway(), gateway);

        var output = useCase.execute(saved.id());

        assertEquals(PaymentStatus.FAILED, output.transaction().status());
        assertEquals("Pagamento recusado pela operadora.", output.transaction().failureReason());
    }

    @Test
    void shouldReturnExpiredPaymentStatus() {
        InMemoryPaymentTransactionGateway gateway = new InMemoryPaymentTransactionGateway();
        PaymentTransaction saved = gateway.save(transaction(PaymentStatus.PROCESSING, null));
        GetPaymentStatusUseCase useCase = new GetPaymentStatusUseCase(new ExpiredRefreshGateway(), gateway);

        var output = useCase.execute(saved.id());

        assertEquals(PaymentStatus.EXPIRED, output.transaction().status());
        assertEquals("Pagamento expirado.", output.transaction().failureReason());
    }

    @Test
    void shouldSurfaceCommunicationErrorWhenRefreshingPayment() {
        InMemoryPaymentTransactionGateway gateway = new InMemoryPaymentTransactionGateway();
        PaymentTransaction saved = gateway.save(transaction(PaymentStatus.PROCESSING, null));
        GetPaymentStatusUseCase useCase = new GetPaymentStatusUseCase(new CommunicationErrorGateway(), gateway);

        assertThrows(IllegalStateException.class, () -> useCase.execute(saved.id()));
    }

    @Test
    void shouldSurfaceCommunicationErrorWhenConfirmingPayment() {
        InMemoryPaymentTransactionGateway gateway = new InMemoryPaymentTransactionGateway();
        PaymentTransaction saved = gateway.save(transaction(PaymentStatus.PROCESSING, null));
        ConfirmPaymentUseCase useCase = new ConfirmPaymentUseCase(new CommunicationErrorGateway(), gateway);

        assertThrows(IllegalStateException.class, () -> useCase.execute(saved.id()));
    }

    @Test
    void shouldFailWhenTransactionDoesNotExist() {
        GetPaymentStatusUseCase useCase = new GetPaymentStatusUseCase(new SuccessfulStartGateway(), new InMemoryPaymentTransactionGateway());

        assertThrows(NotFoundException.class, () -> useCase.execute(999L));
    }

    private static PaymentTransaction transaction(PaymentStatus status, String failureReason) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(null, "fake", "fake-ref", PaymentMethod.PIX, status, 19.9, failureReason, now.plusMinutes(15), null, now, now, null);
    }

    private static final class InMemoryPaymentTransactionGateway implements PaymentTransactionGateway {
        private final Map<Long, PaymentTransaction> transactions = new HashMap<>();
        private long nextId = 1;

        @Override
        public PaymentTransaction save(PaymentTransaction transaction) {
            PaymentTransaction saved = new PaymentTransaction(
                    transaction.id() == null ? nextId++ : transaction.id(),
                    transaction.provider(),
                    transaction.providerReference(),
                    transaction.method(),
                    transaction.status(),
                    transaction.amount(),
                    transaction.failureReason(),
                    transaction.expiresAt(),
                    transaction.confirmedAt(),
                    transaction.createdAt(),
                    transaction.updatedAt(),
                    transaction.orderId()
            );
            transactions.put(saved.id(), saved);
            return saved;
        }

        @Override
        public Optional<PaymentTransaction> findById(Long id) {
            return Optional.ofNullable(transactions.get(id));
        }
    }

    private static class SuccessfulStartGateway implements PaymentGateway {
        @Override
        public PaymentProviderResult start(PaymentMethod method, double amount) {
            return new PaymentProviderResult("fake", "fake-start", PaymentStatus.PROCESSING, null, LocalDateTime.now().plusMinutes(15), null);
        }

        @Override
        public PaymentProviderResult refresh(PaymentTransaction transaction) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.PAID, null, transaction.expiresAt(), LocalDateTime.now());
        }

        @Override
        public PaymentProviderResult confirm(PaymentTransaction transaction) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.PAID, null, transaction.expiresAt(), LocalDateTime.now());
        }
    }

    private static final class FailedRefreshGateway extends SuccessfulStartGateway {
        @Override
        public PaymentProviderResult refresh(PaymentTransaction transaction) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.FAILED, "Pagamento recusado pela operadora.", transaction.expiresAt(), null);
        }
    }

    private static final class ExpiredRefreshGateway extends SuccessfulStartGateway {
        @Override
        public PaymentProviderResult refresh(PaymentTransaction transaction) {
            return new PaymentProviderResult(transaction.provider(), transaction.providerReference(), PaymentStatus.EXPIRED, "Pagamento expirado.", transaction.expiresAt(), null);
        }
    }

    private static final class CommunicationErrorGateway extends SuccessfulStartGateway {
        @Override
        public PaymentProviderResult refresh(PaymentTransaction transaction) {
            throw new IllegalStateException("Erro de comunicacao com o provedor de pagamento.");
        }

        @Override
        public PaymentProviderResult confirm(PaymentTransaction transaction) {
            throw new IllegalStateException("Erro de comunicacao com o provedor de pagamento.");
        }
    }
}
