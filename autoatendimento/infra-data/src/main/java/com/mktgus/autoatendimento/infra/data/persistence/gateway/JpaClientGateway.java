package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.infra.data.persistence.mapper.CustomerEntityMapper;
import com.mktgus.autoatendimento.infra.data.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaClientGateway implements ClientGateway {
    private final CustomerRepository customerRepository;

    public JpaClientGateway(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findByCpf(Long cpf) {
        return customerRepository.findByCpf(cpf).map(CustomerEntityMapper::toDomain);
    }

    @Override
    public Customer save(Customer client) {
        return CustomerEntityMapper.toDomain(customerRepository.save(CustomerEntityMapper.toEntity(client)));
    }
}
