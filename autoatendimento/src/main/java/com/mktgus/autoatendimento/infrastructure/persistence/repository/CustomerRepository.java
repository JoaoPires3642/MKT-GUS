package com.mktgus.autoatendimento.infrastructure.persistence.repository;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByCpf(Long cpf);
}
