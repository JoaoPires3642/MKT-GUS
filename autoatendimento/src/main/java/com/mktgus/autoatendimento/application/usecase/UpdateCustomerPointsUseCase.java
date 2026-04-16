package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.UpdateCustomerPointsInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class UpdateCustomerPointsUseCase {
    private final ClientGateway clientGateway;

    public UpdateCustomerPointsUseCase(ClientGateway clientGateway) {
        this.clientGateway = clientGateway;
    }

    public void execute(UpdateCustomerPointsInput input) {
        if (input.pointsBalance() < 0) {
            throw new ValidationException("Saldo de pontos nao pode ser negativo.");
        }

        Long cpf = parseCpf(input.cpf());
        Customer client = clientGateway.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado para o CPF informado."));

        clientGateway.save(client.withPoints(input.pointsBalance()));
    }

    private Long parseCpf(String rawCpf) {
        try {
            return Long.parseLong(rawCpf.replaceAll("\\D", ""));
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido.");
        }
    }
}
