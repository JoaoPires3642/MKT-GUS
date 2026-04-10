package com.mktgus.autoatendimento.domain.model;

public record Customer(Long cpf, int points) {
    public Customer withPoints(int newPoints) {
        return new Customer(cpf, newPoints);
    }
}
