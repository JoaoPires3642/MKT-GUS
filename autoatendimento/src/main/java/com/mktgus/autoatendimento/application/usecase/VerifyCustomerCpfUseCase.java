package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.VerifyCustomerCpfInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class VerifyCustomerCpfUseCase {
    private final ClientGateway clientGateway;

    public VerifyCustomerCpfUseCase(ClientGateway clientGateway) {
        this.clientGateway = clientGateway;
    }

    public Customer execute(VerifyCustomerCpfInput input) {
        Long cpf = parseCpf(input.cpf());
        return clientGateway.findByCpf(cpf).orElseGet(() -> clientGateway.save(new Customer(cpf, 0)));
    }

    private Long parseCpf(String rawCpf) {
        try {
            return Long.parseLong(rawCpf.replaceAll("\\D", ""));
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido.");
        }
    }
}
