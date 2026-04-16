package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.VerifyCustomerCpfInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerifyCustomerCpfUseCaseTest {
    @Test
    void shouldCreateCustomerWhenCpfDoesNotExist() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        VerifyCustomerCpfUseCase useCase = new VerifyCustomerCpfUseCase(clientGateway);

        Customer customer = useCase.execute(new VerifyCustomerCpfInput("123.456.789-00"));

        assertEquals(12345678900L, customer.cpf());
        assertEquals(0, customer.points());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidCpf() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        VerifyCustomerCpfUseCase useCase = new VerifyCustomerCpfUseCase(clientGateway);

        assertThrows(ValidationException.class, () -> useCase.execute(new VerifyCustomerCpfInput("abc")));
    }

    private static final class InMemoryClientGateway implements ClientGateway {
        private final Map<Long, Customer> customers = new HashMap<>();

        @Override
        public Optional<Customer> findByCpf(Long cpf) {
            return Optional.ofNullable(customers.get(cpf));
        }

        @Override
        public Customer save(Customer client) {
            customers.put(client.cpf(), client);
            return client;
        }
    }
}
