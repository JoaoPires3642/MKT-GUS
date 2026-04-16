package com.mktgus.autoatendimento.domain.gateway;

public interface EmployeeGateway {
    boolean existsByRegistration(Long registration);
}
