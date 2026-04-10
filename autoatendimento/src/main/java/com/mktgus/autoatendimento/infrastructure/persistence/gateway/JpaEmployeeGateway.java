package com.mktgus.autoatendimento.infrastructure.persistence.gateway;

import com.mktgus.autoatendimento.domain.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.infrastructure.persistence.repository.EmployeeRepository;
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
