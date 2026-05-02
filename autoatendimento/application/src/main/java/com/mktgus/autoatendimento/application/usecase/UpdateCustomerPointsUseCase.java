package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.points.UpdateCustomerPointsInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.ClientGateway;
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
        if (rawCpf == null || rawCpf.isBlank()) {
            throw new ValidationException("CPF invalido.");
        }
        String onlyDigits = rawCpf.replaceAll("\\D", "");
        if (onlyDigits.length() != 11) {
            throw new ValidationException("CPF invalido.");
        }
        try {
            return Long.parseLong(onlyDigits);
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido.");
        }
    }
}
