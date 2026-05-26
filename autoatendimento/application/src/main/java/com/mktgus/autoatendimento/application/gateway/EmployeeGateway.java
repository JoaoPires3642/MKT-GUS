package com.mktgus.autoatendimento.application.gateway;

import java.util.Optional;

public interface EmployeeGateway {
    boolean existsByRegistration(Long registration);
    Optional<EmployeeInfo> findByRegistration(Long registration);
}
