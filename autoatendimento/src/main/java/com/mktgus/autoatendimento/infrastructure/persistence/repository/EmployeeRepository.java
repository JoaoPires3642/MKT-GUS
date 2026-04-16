package com.mktgus.autoatendimento.infrastructure.persistence.repository;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    boolean existsByMatricula(Long matricula);
}
