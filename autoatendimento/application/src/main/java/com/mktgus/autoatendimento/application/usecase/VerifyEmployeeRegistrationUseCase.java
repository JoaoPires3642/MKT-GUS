package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.application.gateway.EmployeeInfo;
import com.mktgus.autoatendimento.application.verification.VerifyEmployeeRegistrationInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerifyEmployeeRegistrationUseCase {
    private final EmployeeGateway employeeGateway;

    public VerifyEmployeeRegistrationUseCase(EmployeeGateway employeeGateway) {
        this.employeeGateway = employeeGateway;
    }

    public Optional<EmployeeInfo> execute(VerifyEmployeeRegistrationInput input) {
        try {
            return employeeGateway.findByRegistration(Long.parseLong(input.registration()));
        } catch (NumberFormatException exception) {
            throw new ValidationException("Matricula invalida.");
        }
    }
}
