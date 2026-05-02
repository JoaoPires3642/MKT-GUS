package com.mktgus.autoatendimento.domain.model;

public record Customer(Long cpf, int points) {
    public Customer {
        if (cpf == null || cpf <= 0) throw new IllegalArgumentException("CPF inválido");
        if (points < 0) throw new IllegalArgumentException("Pontos não podem ser negativos");
    }

    public Customer withPoints(int newPoints) {
        return new Customer(cpf, newPoints);
    }

    public Customer earnPoints(int earned) {
        if (earned < 0) throw new IllegalArgumentException("Pontos ganhos não podem ser negativos");
        return new Customer(cpf, points + earned);
    }

    public boolean hasEnoughPoints(int required) {
        return points >= required;
    }
}
