package com.mktgus.autoatendimento.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CustomerEntity {

    @Id  // CPF será a chave primária
    private Long cpf;  // Não precisa do @GeneratedValue, pois o CPF é fornecido pelo usuário

    private int pontos;

    public CustomerEntity(Long cpf, int pontos) {
        this.cpf = cpf;
        this.pontos = pontos;
    }

    // Getters e setters
    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }
}
