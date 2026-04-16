package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.VerifyEmployeeRegistrationInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.EmployeeGateway;
import org.springframework.stereotype.Service;

@Service
public class VerifyEmployeeRegistrationUseCase {
    private final EmployeeGateway employeeGateway;

    public VerifyEmployeeRegistrationUseCase(EmployeeGateway employeeGateway) {
        this.employeeGateway = employeeGateway;
    }

    public boolean execute(VerifyEmployeeRegistrationInput input) {
        try {
            return employeeGateway.existsByRegistration(Long.parseLong(input.registration()));
        } catch (NumberFormatException exception) {
            throw new ValidationException("Matricula invalida.");
        }
    }
}
