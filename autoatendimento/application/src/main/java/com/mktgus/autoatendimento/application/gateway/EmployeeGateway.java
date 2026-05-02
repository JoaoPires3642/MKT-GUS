package com.mktgus.autoatendimento.application.gateway;

public interface EmployeeGateway {
    boolean existsByRegistration(Long registration);
}
