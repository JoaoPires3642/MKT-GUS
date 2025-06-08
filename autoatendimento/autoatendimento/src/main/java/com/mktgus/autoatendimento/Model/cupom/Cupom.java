package com.mktgus.autoatendimento.Model.cupom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 100)
    private String descricao;

    @Column(nullable = false)
    @Min(value = 0, message = "Valor do desconto deve ser positivo")
    private double valorDesconto;

    @Column(nullable = false)
    private boolean descontoEmPorcentual;

    @Column(nullable = false)
    @Min(value = 2, message = "Custo do cupom em pontos deve ser no mínimo 2")
    private int custo;

    @Column(nullable = true)
    @Min(value = 0, message = "Valor mínimo de compra deve ser positivo")
    private Double minPurchase;

    @Column(nullable = true)
    @Min(value = 0, message = "Desconto máximo deve ser positivo")
    private Double maxDiscount;
}