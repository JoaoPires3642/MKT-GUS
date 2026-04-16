package com.mktgus.autoatendimento.infrastructure.persistence.mapper;

import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.CustomerEntity;

public final class CustomerEntityMapper {
    private CustomerEntityMapper() {
    }

    public static Customer toDomain(CustomerEntity entity) {
        return new Customer(entity.getCpf(), entity.getPontos());
    }

    public static CustomerEntity toEntity(Customer domain) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCpf(domain.cpf());
        entity.setPontos(domain.points());
        return entity;
    }
}
