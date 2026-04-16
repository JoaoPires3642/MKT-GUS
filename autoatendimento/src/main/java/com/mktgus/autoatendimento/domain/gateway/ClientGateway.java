package com.mktgus.autoatendimento.domain.gateway;

import com.mktgus.autoatendimento.domain.model.Customer;

import java.util.Optional;

public interface ClientGateway {
    Optional<Customer> findByCpf(Long cpf);

    Customer save(Customer client);
}
