package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.infra.data.persistence.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaEmployeeGateway implements EmployeeGateway {
    private final EmployeeRepository employeeRepository;

    public JpaEmployeeGateway(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public boolean existsByRegistration(Long registration) {
        return employeeRepository.existsByMatricula(registration);
    }
}
